package com.neuralmap.backend.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.neuralmap.backend.exception.DocumentReadException;
import com.neuralmap.backend.exception.InvalidDocumentException;
import com.neuralmap.backend.support.PdfTestUtil;

class TextExtractionServiceTest {

    // JUnit gives each test a fresh empty folder and deletes it afterwards.
    @TempDir
    Path tempDir;

    @Test
    void extractsEachPageWithItsNumber() {
        UUID id = UUID.randomUUID();
        PdfTestUtil.createPdf(tempDir.resolve(id + ".pdf"),
                "Photosynthesis turns light into energy.",
                "Mitochondria produce energy for the cell.");

        List<PageText> pages = new TextExtractionService(tempDir.toString()).extract(id);

        assertEquals(2, pages.size());
        assertEquals(1, pages.get(0).pageNumber());
        assertEquals(2, pages.get(1).pageNumber());
        assertTrue(pages.get(0).text().contains("Photosynthesis"));
        assertTrue(pages.get(1).text().contains("Mitochondria"));
        assertFalse(pages.get(0).lowText());
    }

    @Test
    void marksShortAndBlankPagesAsLowText() {
        UUID id = UUID.randomUUID();
        PdfTestUtil.createPdf(tempDir.resolve(id + ".pdf"),
                "This page has plenty of readable text.",
                null,       // blank page
                "Short");   // under the 20 character minimum

        List<PageText> pages = new TextExtractionService(tempDir.toString()).extract(id);

        assertFalse(pages.get(0).lowText());
        assertTrue(pages.get(1).lowText());
        assertTrue(pages.get(2).lowText());
    }

    @Test
    void rejectsPdfWithNoReadableText() {
        UUID id = UUID.randomUUID();
        PdfTestUtil.createPdf(tempDir.resolve(id + ".pdf"), (String) null, (String) null);

        assertThrows(InvalidDocumentException.class,
                () -> new TextExtractionService(tempDir.toString()).extract(id));
    }

    @Test
    void failsWhenTheFileIsMissing() {
        assertThrows(DocumentReadException.class,
                () -> new TextExtractionService(tempDir.toString()).extract(UUID.randomUUID()));
    }

    @Test
    void failsWhenTheFileIsNotARealPdf() throws Exception {
        UUID id = UUID.randomUUID();
        Files.writeString(tempDir.resolve(id + ".pdf"), "just some text");

        assertThrows(DocumentReadException.class,
                () -> new TextExtractionService(tempDir.toString()).extract(id));
    }
}
