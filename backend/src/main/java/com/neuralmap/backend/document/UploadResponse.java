package com.neuralmap.backend.document;

/** What the backend sends back after an upload. */
public record UploadResponse(String fileName, long sizeBytes) {
}
