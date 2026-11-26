package com.neuralmap.backend.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ConceptPromptTest {

    private final ConceptPrompt prompt = new ConceptPrompt();

    @Test
    void promptContainsTheChunkTextPagesAndCategories() {
        String text = prompt.build(new TextChunk(5, 8, "Dependency Injection is a design pattern."));

        assertTrue(text.contains("Dependency Injection is a design pattern."));
        assertTrue(text.contains("pages 5 to 8"));
        assertTrue(text.contains("at most " + ConceptPrompt.MAX_CONCEPTS + " concepts"));
        assertTrue(text.contains("data_structure"));
        assertTrue(text.contains("method"));
    }

    @Test
    @SuppressWarnings("unchecked") // the schema is nested Maps, so we cast while walking into it
    void schemaLimitsCountAndAllowsOnlyKnownCategories() {
        Map<String, Object> concepts = (Map<String, Object>) ((Map<String, Object>) prompt.schema().get("properties")).get("concepts");
        Map<String, Object> item = (Map<String, Object>) concepts.get("items");
        Map<String, Object> properties = (Map<String, Object>) item.get("properties");
        Map<String, Object> category = (Map<String, Object>) properties.get("category");

        assertEquals(ConceptPrompt.MAX_CONCEPTS, concepts.get("maxItems"));
        assertEquals(List.of("name", "definition", "category", "evidence"), List.copyOf(properties.keySet()));
        assertEquals(ConceptCategory.values().length, ((List<String>) category.get("enum")).size());
        assertTrue(((List<String>) category.get("enum")).contains("other"));
    }
}
