package com.neuralmap.backend.extraction;

/**
 * A checked concept, ready for the graph.
 *
 * @param id         unique and stable, made from the name (e.g. "dependency-injection")
 * @param evidence   a quote that really appears in the PDF, or null if the model's quote could not be found
 * @param startPage  the page of the evidence; if evidence is null, the first page of the chunk
 * @param endPage    same as startPage when the evidence was found, otherwise the last page of the chunk
 */
public record Concept(
        String id,
        String name,
        String definition,
        ConceptCategory category,
        String evidence,
        int startPage,
        int endPage) {
}
