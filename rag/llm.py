from llama_index.core import VectorStoreIndex
from llama_index.llms.ollama import Ollama
from llama_index.embeddings.ollama import OllamaEmbedding
from llama_index.core import VectorStoreIndex
from llama_index.storage.chat_store.redis import RedisChatStore
from llama_index.core.memory import ChatMemoryBuffer
from llama_index.core.chat_engine.types import BaseChatEngine
from llama_index.core.workflow import (
    Workflow,
    StartEvent,
    StopEvent,
    Event,
    step,
)

from llama_index.vector_stores.redis import RedisVectorStore
from llama_index.core import StorageContext
from llama_index.core.indices.loading import load_index_from_storage

from redis import Redis
from redisvl.schema import IndexSchema

import logging
from typing import Union

from .utils import load_data
from .prompts import SYSTEM_PROMPT, INTRO_USER_PROMPT

llm = None
embedding = None
redis_client = None
chat_store = None
storage_context = None
verbose = False


def init_llm(
    llm_model: str,
    temp: float = 0.1,
    context_window: int = 3900,
) -> None:
    global llm
    llm = Ollama(
        model=llm_model,
        temperature=temp,
        context_window=context_window,
    )


def init_embedding(embed_model: str):
    global embedding
    embedding = OllamaEmbedding(model_name=embed_model)


def init_redis_client(redis_url):
    global redis_client
    redis_client = Redis.from_url(redis_url)


def init_chat_store(redis_url, ttl=300) -> None:
    global chat_store
    chat_store = RedisChatStore(redis_url=redis_url, redis_client=redis_client, ttl=ttl)


def init_vector_store(vector_dims: int) -> None:
    global storage_context
    custom_schema = IndexSchema.from_dict(
        {
            "index": {"name": "redis_vector_store", "prefix": "doc"},
            "fields": [
                {"type": "tag", "name": "id"},
                {"type": "tag", "name": "doc_id"},
                {"type": "text", "name": "text"},
                {"type": "tag", "name": "file_name"},
                {
                    "type": "vector",
                    "name": "vector",
                    "attrs": {
                        "dims": vector_dims,
                        "algorithm": "hnsw",
                        "distance_metric": "cosine",
                    },
                },
            ],
        }
    )

    vector_store = RedisVectorStore(
        redis_client=redis_client, schema=custom_schema, overwrite=True
    )
    storage_context = StorageContext.from_defaults(vector_store=vector_store)


def new_chat_engine(
    session_id: str, file_urls: list[str], token_limit: int = 3000
) -> BaseChatEngine:
    global chat_store
    data = load_data(file_urls)

    index = VectorStoreIndex.from_documents(
        data,
        embed_model=embedding,
        storage_context=storage_context,
    )
    index.set_index_id(session_id)

    chat_store.delete_messages(session_id)
    chat_memory = ChatMemoryBuffer.from_defaults(
        token_limit=token_limit,
        chat_store=chat_store,
        chat_store_key=session_id,
    )
    chat_engine = index.as_chat_engine(
        chat_mode="condense_plus_context",
        memory=chat_memory,
        llm=llm,
        verbose=verbose,
        system_prompt=SYSTEM_PROMPT,
    )
    return chat_engine


def get_chat_engine(session_id: str, token_limit: int = 3000) -> BaseChatEngine:
    index = load_index_from_storage(
        storage_context=storage_context, index_id=session_id, embed_model=embedding
    )
    chat_memory = ChatMemoryBuffer.from_defaults(
        token_limit=token_limit,
        chat_store=chat_store,
        chat_store_key=session_id,
    )
    chat_engine = index.as_chat_engine(
        chat_mode="condense_plus_context",
        memory=chat_memory,
        llm=llm,
        verbose=verbose,
        system_prompt=SYSTEM_PROMPT,
    )
    return chat_engine


class FailedEvent(Event):
    error: str


class SetupEvent(Event):
    session_id: str


class QueryEvent(Event):
    query: str
    session_id: str


class KhojChatEngineWorkflow(Workflow):
    @step
    async def setup(self, ev: StartEvent) -> Union[StopEvent, FailedEvent, QueryEvent]:
        if hasattr(ev, "setup"):
            if hasattr(ev, "session_id") and hasattr(ev, "file_urls"):
                chat_engine = new_chat_engine(ev.session_id, ev.file_urls)
                response = await chat_engine.achat(INTRO_USER_PROMPT)
                return StopEvent(result=str(response))
            else:
                return FailedEvent(error="missing session_id or file_urls attributes")

        if hasattr(ev, "query") and hasattr(ev, "session_id"):
            return QueryEvent(query=ev.query, session_id=ev.session_id)

        return FailedEvent(error="missing session_id or query attributes")

    @step
    async def query(self, ev: QueryEvent) -> Union[StopEvent, FailedEvent]:
        chat_engine = get_chat_engine(ev.session_id)
        if chat_engine:
            response = await chat_engine.achat(ev.query)
            return StopEvent(result=str(response))
        else:
            return FailedEvent(
                error=f"chat engine not initialised against session id: {ev.session_id}"
            )

    @step
    async def error(self, ev: FailedEvent) -> StopEvent:
        if hasattr(ev, "error"):
            logging.error(ev.error)
        return StopEvent(result=None)


workflow = KhojChatEngineWorkflow(timeout=100)


def init(config):
    global verbose, workflow
    verbose = True if int(config["VERBOSE"]) else False
    init_llm(llm_model=config["LLM_MODEL"])
    init_embedding(embed_model=config["EMBED_MODEL"])
    init_redis_client(config["REDIS_URL"])
    init_chat_store(config["REDIS_URL"], config.get("TTL", None))
    init_vector_store(config["VECTOR_DIMS"])
