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

- Chunking: `ChunkingService` merges pages into chunks of up to 6000 chars (`TextChunk(startPage, endPage, text)`), skips low-text pages
- Ollama context window set to 8192 tokens (`app.ollama.context-size`)
- Speed test (`ConceptSpeedCheck`, manual): ~70-120 s per chunk on CPU. 35-page summary: 9 chunks, ~15 min for concepts. 41-slide deck: 5 chunks, ~9 min. Time depends mostly on the number of calls and the answer length.

## Next, in order
3. **Concept extraction**: prompt + JSON schema per chunk (category `enum`, ~8 concepts max); validate output (required fields, no empty definitions); check that the evidence quote really appears in the text (ignore case and extra spaces, reject quotes with "...") and use it to find the page; merge duplicate concepts (case-insensitive names).
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
