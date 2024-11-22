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
verbose = False
vector_dims = 0


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


def get_storage_context(session_id: str, overwrite: bool) -> StorageContext:
    custom_schema = IndexSchema.from_dict(
        {
            "index": {"name": session_id, "prefix": "doc"},
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
        redis_client=redis_client, schema=custom_schema, overwrite=overwrite
    )
    return StorageContext.from_defaults(vector_store=vector_store)


def new_chat_engine(
    session_id: str, file_urls: list[str], token_limit: int = 3000
) -> BaseChatEngine:
    global chat_store
    data = load_data(file_urls)

    storage_context = get_storage_context(session_id=session_id, overwrite=True)

    index = VectorStoreIndex.from_documents(
        data,
        embed_model=embedding,
        storage_context=storage_context,
    )
    
    storage_context.index_store.delete_index_struct(session_id)
    index.set_index_id(session_id)

    retriever = index.as_retriever(similarity_top_k=10000)

    # retrieve all nodes
    # all_nodes = retriever.retrieve("Whatever")
    # [print(str(item.node) + "\n\n") for item in all_nodes]

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
    
    storage_context = get_storage_context(session_id=session_id, overwrite=False)
    index = VectorStoreIndex.from_vector_store(storage_context.vector_store, embed_model=embedding)
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
    global verbose, workflow, vector_dims
    verbose = True if int(config["VERBOSE"]) else False
    init_llm(llm_model=config["LLM_MODEL"])
    init_embedding(embed_model=config["EMBED_MODEL"])
    init_redis_client(config["REDIS_URL"])
    init_chat_store(config["REDIS_URL"], config.get("TTL", None))
    vector_dims = int(config["VECTOR_DIMS"])
