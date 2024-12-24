FROM python:3.9-alpine

WORKDIR /app
COPY . .
COPY ../.env .
RUN apk update && apk add --no-cache gcc libc-dev g++ rust cargo
RUN pip install --upgrade pip
RUN pip install --no-cache-dir --upgrade -r requirements.txt

CMD ["fastapi", "run", "main.py", "--port", "8081"]