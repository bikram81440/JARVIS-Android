# JARVIS Secure Backend Service

A secure Node.js & Express backend API layer for the JARVIS Android assistant. It proxies requests to the OpenAI API securely using server-side environment variables (`OPENAI_API_KEY`), keeping sensitive credentials away from the client-side APK/AAB.

## Setup & Deployment

1. Install dependencies:
   ```bash
   npm install
   ```
2. Configure environment variables:
   Copy `.env.example` to `.env` and set your `OPENAI_API_KEY`.
3. Start the server:
   ```bash
   npm start
   ```

## Endpoints

- `GET /health` - Health check status
- `POST /api/chat` - Secure proxy to OpenAI chat completions
