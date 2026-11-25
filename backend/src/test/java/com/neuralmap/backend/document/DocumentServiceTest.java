package com.neuralmap.backend.document;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import com.neuralmap.backend.exception.DocumentNotFoundException;
import com.neuralmap.backend.exception.InvalidDocumentException;
import com.neuralmap.backend.support.PdfTestUtil;

class DocumentServiceTest {

    @TempDir
    Path tempDir;

    DocumentService service;
    DocumentStore store;

    // Runs before every test: builds the service by hand with the real classes (no Spring needed).
    @BeforeEach
    void setUp() {
        store = new DocumentStore();
        service = new DocumentService(
                new FileStorageService(tempDir.toString()),
                new TextExtractionService(tempDir.toString()),
                store);
    }

    private MockMultipartFile file(byte[] content) {
        return new MockMultipartFile("file", "notes.pdf", "application/pdf", content);
    }

    @Test
    void validateRejectsEmptyFile() {
        assertThrows(InvalidDocumentException.class, () -> service.validate(file(new byte[0])));
    }

    @Test
    void validateRejectsFileWithoutPdfHeader() {
        assertThrows(InvalidDocumentException.class, () -> service.validate(file("hello world".getBytes())));
    }

    @Test
    void validateAcceptsRealPdf() {
        assertDoesNotThrow(() -> service.validate(file(PdfTestUtil.pdfBytes("Some readable text here."))));
    }

    @Test
    void uploadSavesFileAndStoresDocument() {
        Document doc = service.upload(file(PdfTestUtil.pdfBytes("Some readable text here.")));

        assertEquals("notes.pdf", doc.fileName());
        assertEquals(1, doc.pages().size());
        assertTrue(tempDir.resolve(doc.id() + ".pdf").toFile().exists());
        assertEquals(doc, service.get(doc.id()));
    }

    @Test
    void uploadRemovesTheSavedFileWhenExtractionFails() {
        // valid PDF, but no text at all -> extraction throws
        byte[] blank = PdfTestUtil.pdfBytes((String) null);

        assertThrows(InvalidDocumentException.class, () -> service.upload(file(blank)));

        // the file saved before the failure must be cleaned up
        assertEquals(0, tempDir.toFile().list().length);
    }

    @Test
    void getThrowsForUnknownId() {
        assertThrows(DocumentNotFoundException.class, () -> service.get(UUID.randomUUID()));
    }

    @Test
    void loadIDsReloadsSavedPdfsAndSkipsOtherFiles() throws Exception {
        UUID id = UUID.randomUUID();
        PdfTestUtil.createPdf(tempDir.resolve(id + ".pdf"), "Saved earlier, readable text.");
        java.nio.file.Files.writeString(tempDir.resolve("notes.txt"), "not a pdf");

        service.loadIDs();

        assertTrue(store.find(id).isPresent());
        assertFalse(store.find(UUID.randomUUID()).isPresent());
    }
}
