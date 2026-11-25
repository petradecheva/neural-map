# NeuralMap Dev Plan

Last updated: 2026-11-25

## Done
- Spring Boot backend (Gradle, Java 17), health check
- PDF upload with validation (`POST /api/documents`), global error handling
- File storage under a UUID, reload of saved documents at startup
- Page-by-page text extraction with low-text detection (`GET /api/documents/{id}/pages`)
- In-memory document store, 404 for unknown ids
- Tests for extraction, document service and controller
- AI client: `AiClient` interface + `OllamaClient` (RestClient, JSON schema output), 503 when the model is unavailable

## Next, in order
1. **Chunking**: `ChunkingService` splits page text into small chunks (max 1500 chars), keeps the page number, skips low-text pages.
2. **Speed test**: run one concept prompt on one real chunk from a real lecture PDF and time it. Decide chunk size, model and demo PDF size from the result.
3. **Concept extraction**: prompt + JSON schema per chunk; validate output (required fields, no empty definitions), check that the evidence quote really appears in the chunk, merge duplicate concepts.
4. **Background processing**: run AI work in the background, document status (`PROCESSING`, `DONE`, `FAILED`) with an endpoint to poll it, save results as a JSON file next to the PDF.
5. **Thin frontend slice**: React + Vite + React Flow; upload a PDF and show the concepts as nodes.
6. **Relationships**: second AI pass, validate types and concept references, show as edges.
7. **Details and documents**: concept details panel with sources, `GET /api/documents` list, `DELETE /api/documents/{id}`.
8. **AI chat (FR-09)**: answers grounded in the PDF; start with simple context (concept, evidence, page chunks).

## Decisions
- Plain Spring `RestClient` for Ollama instead of Spring AI or LangChain4j. Revisit for the AI chat if needed.
- PostgreSQL later (P1); JSON files on disk until then.
- A cloud model is only an opt-in fallback if the local model is too slow, and needs a product decision first.

## Known small issues
- `loadIDs` loses the original file name and size after a restart.
