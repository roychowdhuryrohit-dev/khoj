FROM ollama/ollama:0.5.4

COPY ../.env /tmp/.env
COPY ./run.sh /tmp/run.sh

WORKDIR /tmp

RUN chmod +x run.sh && ./run.sh

EXPOSE 11434