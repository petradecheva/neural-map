package com.neuralmap.backend.extraction;

/**
 * One concept exactly as the model returned it. Nothing here is trusted yet:
 * every field can be missing, empty or wrong until ConceptValidator has checked it.
 */
public record RawConcept(String name, String definition, String category, String evidence) {
}
