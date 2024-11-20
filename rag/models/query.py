from pydantic import BaseModel

class Query(BaseModel):
    prompt: str 
    session_id: str

class QueryResponse(BaseModel):
    message: str

class SessionInit(BaseModel):
    session_id: str
    file_urls: list[str]
