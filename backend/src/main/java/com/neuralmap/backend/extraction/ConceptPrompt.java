package com.neuralmap.backend.extraction;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Builds the prompt and the JSON schema for concept extraction.
 * Kept separate from the service so prompt changes never touch the processing logic.
 */
@Component
public class ConceptPrompt {

    static final int MAX_CONCEPTS = 8;

    private static final List<String> CATEGORIES = Arrays.stream(ConceptCategory.values())
            .map(ConceptCategory::jsonValue)
            .toList();

    private static final String TEMPLATE = """
            You extract the key concepts from a piece of lecture text for a study knowledge graph.
            List at most %d concepts: only the most important ones that the text actually explains.
            Do not invent anything. Ignore headers, footers, page numbers and "Downloaded by" lines.
            For each concept give:
            - name: a short name (1 to 4 words)
            - definition: at most 20 words, based only on the text
            - category: the best fitting one of: %s (use "other" if none fits)
            - evidence: one sentence or phrase copied exactly from the text, at most 25 words.
              Copy it word for word. Never shorten it with "..." and never add words.
            Keep the language of the text.

            Text from pages %d to %d:
            \"\"\"
            %s
            \"\"\"
            """;

    private static final Map<String, Object> SCHEMA = buildSchema();

    /** The full prompt for one chunk. */
    public String build(TextChunk chunk) {
        return TEMPLATE.formatted(
                MAX_CONCEPTS,
                String.join(", ", CATEGORIES),
                chunk.startPage(),
                chunk.endPage(),
                chunk.text());
    }

    /** The JSON schema the model's answer must follow. */
    public Map<String, Object> schema() {
        return SCHEMA;
    }

    private static Map<String, Object> buildSchema() {
        // LinkedHashMap keeps the order, so the model writes name -> definition -> category -> evidence.
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("name", Map.of("type", "string"));
        properties.put("definition", Map.of("type", "string"));
        properties.put("category", Map.of("type", "string", "enum", CATEGORIES));
        properties.put("evidence", Map.of("type", "string"));

        Map<String, Object> concept = Map.of(
                "type", "object",
                "properties", properties,
                "required", List.of("name", "definition", "category", "evidence"));

        return Map.of(
                "type", "object",
                "properties", Map.of("concepts", Map.of(
                        "type", "array",
                        "maxItems", MAX_CONCEPTS,
                        "items", concept)),
                "required", List.of("concepts"));
    }
}
