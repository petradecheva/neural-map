package com.neuralmap.backend.document;

import java.util.List;
import java.util.UUID;

public record Document(UUID id, String fileName, long sizeBytes, List<PageText> pages) {
}
