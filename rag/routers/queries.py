from fastapi import APIRouter, HTTPException

from typing import Any

from ..models.query import Query, QueryResponse, SessionInit
from ..llm import workflow

router = APIRouter()


@router.post("/sendQuery", response_model=QueryResponse)
async def send_query(query: Query) -> Any:
    res = await workflow.run(session_id=query.session_id, query=query.prompt)
    if res:
        return QueryResponse(message=res)
    raise HTTPException(status_code=500)


@router.post("/startSession", response_model=QueryResponse)
async def start_session(sessionInit: SessionInit) -> Any:
    res = await workflow.run(
        setup=1, session_id=sessionInit.session_id, file_urls=sessionInit.file_urls
    )
    if res:
        return QueryResponse(message=res)
    raise HTTPException(status_code=500)
