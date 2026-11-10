package com.neuralmap.backend.document;

import java.util.UUID;

/** What the backend sends back after an upload. */
public record UploadResponse(UUID id, String fileName, long sizeBytes, int pageCount) {
}
