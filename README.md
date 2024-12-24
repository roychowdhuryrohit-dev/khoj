# Khoj
<img align="right" width="100px" src="https://github.com/roychowdhuryrohit-dev/khoj/blob/main/web/src/assets/detective.png">
Khoj is an open-source, full-stack web application powered by self-hosted large language model and embedding model for Retrieval-Augmented Generation (RAG). It enables gathering valuable insights from documents of various types (e.g., PDF, DOCX, TXT).

## Features

1. **Secured Account Management**: OpenID Connect-based account management (signup, login, recovery, API security) powered by Amazon Cognito.
2. **NoSQL Database**: Powered by Amazon DynamoDB.
3. **CDN File Hosting**: Utilizes AWS S3 with secure, pre-signed, time-limited URLs for accessing documents.
4. **Backend**: Developed with the Java Spring Boot framework and uses session token-based authentication.
5. **Frontend**: Responsive interface built using React, TailwindCSS, and React Markdown. Features include document upload, view, and selection.
6. **Real-time Chat**: WebSocket-based chat powered by STOMP.js, integrated with a large language model.
7. **AI Service**: Built using the FastAPI web framework, leveraging LlamaIndex for interaction with Ollama server and Redis-based vector storage for RAG.
8. **Deployment**: The entire stack can be efficiently deployed and self-hosted using Docker Compose.

## Usage

### Docker Compose

1. Create a `.env` file in the project root containing access keys and secrets for AWS services, the LLM, and embedding models. A sample file (`.env.sample`) is provided for reference.
2. Run the following command:
   ```sh
   docker compose up
   ```

   **Note**: For Nvidia or AMD GPU users, follow the official Ollama instructions to install necessary drivers and update the `docker-compose.yaml` file for improved inference speed during chats.

### Local Development

Local development is recommended, especially for utilizing the Ollama desktop app, which supports Apple Silicon GPUs.

1. Copy the `.env` file to the root directories of the services (`ollama`, `rag`, `web`). Retain a copy in the project root for the `khojapp` service.
2. Run Redis Stack Server and Ollama locally. Ensure that the required LLM and embedding models are pulled into Ollama.

## Future Work

1. **Conversation History**: Currently, the user interface does not store earlier conversations with the LLM. Implementing this feature will allow users to resume old conversations with retained context and selected documents.
2. **Docker GPU Support**: Add a `docker-compose-gpu.yaml` file for faster inference using GPUs.
