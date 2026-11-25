package com.neuralmap.backend.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;    
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * Builds small PDFs for tests, so we don't need PDF files saved in the project.
 * Each text is one page. A null text makes a blank page.
 * Keep the text plain: no line breaks, no accents (the built-in font can't print them).
 */
public final class PdfTestUtil {

    // Only static methods, so nobody should create an object of this class.
    private PdfTestUtil() {
    }

    /** Builds a PDF and returns its bytes (useful for MockMultipartFile). */
    public static byte[] pdfBytes(String... pages) {
        try (PDDocument document = new PDDocument()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            for (String text : pages) {
                // The page is added even when text is null: that is what makes a blank page.
                PDPage page = new PDPage();
                document.addPage(page);

                if (text != null) {
                    try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                        content.beginText();
                        content.setFont(font, 14);
                        content.newLineAtOffset(50, 700);
                        content.showText(text);
                        content.endText();
                    }
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            // A failure here is a broken test setup, so we just let the test fail.
            throw new UncheckedIOException(e);
        }
    }

    /** Builds a PDF and writes it to the given file. */
    public static void createPdf(Path file, String... pages) {
        try {
            Files.write(file, pdfBytes(pages));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
