package com.neuralmap.backend.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.neuralmap.backend.document.PageText;

class ConceptValidatorTest {

    private final ConceptValidator validator = new ConceptValidator();

    // A small "document": the chunk covers pages 1-3, page 4 is outside it.
    private final List<PageText> pages = List.of(
            new PageText(1, "Dependency Injection:\nis a design pattern which an object receives\nother objects that it depends on.", false),
            new PageText(2, "Atomicity: if an error occurs during the execution of the transaction, all operations are rolled back.", false),
            new PageText(3, "Isolation: Concurrently executed transactions do not influence each other.", false),
            new PageText(4, "Durability: Data persisted to the database remains persisted even in case of a system failure.", false));

    private final TextChunk chunk = new TextChunk(1, 3, "text of pages 1 to 3");

    private Concept valid(String name, String category, String evidence) {
        Optional<Concept> result = validator.validate(
                new RawConcept(name, "A short definition.", category, evidence), chunk, pages);
        assertTrue(result.isPresent(), "expected the concept to be accepted");
        return result.get();
    }

    private boolean rejected(RawConcept raw) {
        return validator.validate(raw, chunk, pages).isEmpty();
    }

    // ---- accepted concepts and their fields

    @Test
    void exactEvidenceIsKeptWithItsPage() {
        Concept c = valid("Atomicity", "principle",
                "if an error occurs during the execution of the transaction, all operations are rolled back.");

        assertEquals("atomicity", c.id());
        assertEquals("Atomicity", c.name());
        assertEquals("A short definition.", c.definition());
        assertEquals(ConceptCategory.PRINCIPLE, c.category());
        assertEquals("if an error occurs during the execution of the transaction, all operations are rolled back.", c.evidence());
        assertEquals(2, c.startPage());
        assertEquals(2, c.endPage());
    }

    @Test
    void evidenceMatchesDespiteLineBreaksAndCase() {
        // the PDF text has line breaks, the model writes it on one line with different case
        Concept c = valid("Dependency Injection", "method",
                "Is a design pattern which an object receives other objects that it depends on.");

        assertEquals(1, c.startPage());
        assertEquals(1, c.endPage());
    }

    @Test
    void quotationMarksAroundEvidenceAreRemoved() {
        Concept c = valid("Isolation", "principle",
                "\"Concurrently executed transactions do not influence each other.\"");

        assertEquals("Concurrently executed transactions do not influence each other.", c.evidence());
        assertEquals(3, c.startPage());
    }

    @Test
    void nameIsStrippedAndIdIsMadeFromIt() {
        Concept c = valid("  Dependency Injection (DI) ", "method", null);

        assertEquals("Dependency Injection (DI)", c.name());
        assertEquals("dependency-injection-di", c.id());
    }

    @Test
    void idKeepsLettersFromOtherLanguages() {
        assertEquals("größe-maß", valid("Größe & Maß", "term", null).id());
    }

    // ---- evidence that is not accepted: concept kept, evidence null, page range of the chunk

    private void assertNoEvidence(Concept c) {
        assertNull(c.evidence());
        assertEquals(1, c.startPage());
        assertEquals(3, c.endPage());
    }

    @Test
    void shortenedEvidenceIsDropped() {
        assertNoEvidence(valid("Atomicity", "principle", "Atomicity: if an error occurs... all operations are rolled back."));
        assertNoEvidence(valid("Atomicity", "principle", "Atomicity: if an error occurs… all operations are rolled back."));
    }

    @Test
    void inventedEvidenceIsDropped() {
        assertNoEvidence(valid("Atomicity", "principle", "The ability to undo transactions when subsystems fail."));
    }

    @Test
    void tooShortEvidenceIsDropped() {
        // real text, but only a heading: proves nothing
        assertNoEvidence(valid("Dependency Injection", "method", "Dependency Injection:"));
    }

    @Test
    void missingOrBlankEvidenceIsDropped() {
        assertNoEvidence(valid("Atomicity", "principle", null));
        assertNoEvidence(valid("Atomicity", "principle", "   "));
    }

    @Test
    void evidenceOnAPageOutsideTheChunkIsNotUsed() {
        // this sentence is on page 4, but the chunk only covers pages 1-3
        assertNoEvidence(valid("Durability", "principle", "Data persisted to the database remains persisted"));
    }

    @Test
    void evidenceOnTheLastPageDoesNotCrash() {
        TextChunk lastChunk = new TextChunk(4, 4, "text of page 4");

        Concept c = validator.validate(new RawConcept("Durability", "Def.", "principle",
                "Data persisted to the database remains persisted"), lastChunk, pages).orElseThrow();

        assertEquals(4, c.startPage());
    }

    // ---- categories

    @Test
    void categoryIsReadFromTheCategoryField() {
        assertEquals(ConceptCategory.METHOD, valid("Dependency Injection", "method", null).category());
        assertEquals(ConceptCategory.DATA_STRUCTURE, valid("Hash Map", "data_structure", null).category());
        assertEquals(ConceptCategory.METHOD, valid("Dependency Injection", "  Method ", null).category());
    }

    @Test
    void unknownOrMissingCategoryBecomesOther() {
        assertEquals(ConceptCategory.OTHER, valid("Overconfidence", "bias", null).category());
        assertEquals(ConceptCategory.OTHER, valid("Overconfidence", null, null).category());
    }

    // ---- rejected concepts

    @Test
    void conceptsWithoutNameOrDefinitionAreRejected() {
        assertTrue(rejected(null));
        assertTrue(rejected(new RawConcept(null, "Def.", "term", null)));
        assertTrue(rejected(new RawConcept("   ", "Def.", "term", null)));
        assertTrue(rejected(new RawConcept("Atomicity", null, "term", null)));
        assertTrue(rejected(new RawConcept("Atomicity", "  ", "term", null)));
    }

    @Test
    void nameWithoutLettersOrDigitsIsRejected() {
        assertTrue(rejected(new RawConcept("???", "Def.", "term", null)));
    }
}
