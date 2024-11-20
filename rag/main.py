from fastapi import FastAPI
from .routers import queries
from dotenv import dotenv_values

from .llm import init

init(dotenv_values(".env"))
app = FastAPI()

app.include_router(queries.router)



