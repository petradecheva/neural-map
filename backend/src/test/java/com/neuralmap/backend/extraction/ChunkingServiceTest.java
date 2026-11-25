package com.neuralmap.backend.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.neuralmap.backend.document.PageText;

class ChunkingServiceTest {

    // Same value as ChunkingService.MAX_CHARS (that one is private).
    private static final int MAX_CHARS = 1500;

    private final ChunkingService service = new ChunkingService();

    private static PageText page(int number, String text) {
        return new PageText(number, text, false);
    }

    // n paragraphs of 400 chars each ("aaa...", "bbb...", ...), separated by blank lines
    private static String paragraphs(int n) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(String.valueOf((char) ('a' + i)).repeat(400));
        }
        return String.join("\n\n", list);
    }

    @Test
    void shortPageBecomesOneChunk() {
        List<TextChunk> chunks = service.chunk(List.of(page(1, "Cells are the basic unit of life.")));

        assertEquals(1, chunks.size());
        assertEquals(1, chunks.get(0).pageNumber());
        assertEquals("Cells are the basic unit of life.", chunks.get(0).text());
    }

    @Test
    void lowTextPagesAreSkipped() {
        List<TextChunk> chunks = service.chunk(List.of(
                new PageText(1, "Hi", true),
                page(2, "This page has enough readable text.")));

        assertEquals(1, chunks.size());
        assertEquals(2, chunks.get(0).pageNumber());
    }

    @Test
    void emptyInputGivesNoChunks() {
        assertTrue(service.chunk(List.of()).isEmpty());
        assertTrue(service.chunk(List.of(page(1, "  \n\n   \n"))).isEmpty());
    }

    @Test
    void paragraphsAreGroupedUntilTheLimit() {
        // 3 paragraphs fit (400+2+400+2+400 = 1204), the 4th would go over 1500
        String text = paragraphs(5);

        List<TextChunk> chunks = service.chunk(List.of(page(3, text)));

        assertEquals(2, chunks.size());
        for (TextChunk chunk : chunks) {
            assertTrue(chunk.text().length() <= MAX_CHARS);
            assertEquals(3, chunk.pageNumber());
        }
        // nothing lost and the order is kept
        assertEquals(text, chunks.get(0).text() + "\n\n" + chunks.get(1).text());
    }

    @Test
    void chunksNeverMixPages() {
        List<TextChunk> chunks = service.chunk(List.of(
                page(1, "First page text, quite short."),
                page(2, "Second page text, also short.")));

        assertEquals(2, chunks.size());
        assertEquals(1, chunks.get(0).pageNumber());
        assertEquals(2, chunks.get(1).pageNumber());
    }

    @Test
    void longParagraphIsCutAtSpacesWithoutLosingWords() {
        // one paragraph of about 4000 chars with no blank lines, like many PDFs
        StringBuilder sb = new StringBuilder();
        for (int i = 0; sb.length() < 4000; i++) {
            sb.append("word").append(i).append(' ');
        }
        String text = sb.toString().strip();

        List<TextChunk> chunks = service.chunk(List.of(page(1, text)));

        assertTrue(chunks.size() >= 3);
        List<String> wordsInChunks = new ArrayList<>();
        for (TextChunk chunk : chunks) {
            assertTrue(chunk.text().length() <= MAX_CHARS);
            wordsInChunks.addAll(Arrays.asList(chunk.text().split(" ")));
        }
        // every word is still there, whole and in order (this catches a lost last piece)
        assertEquals(Arrays.asList(text.split(" ")), wordsInChunks);
    }

    @Test
    void textWithoutSpacesIsCutAtTheLimit() {
        String text = "x".repeat(3200);

        List<TextChunk> chunks = service.chunk(List.of(page(1, text)));

        assertEquals(3, chunks.size());
        assertEquals(1500, chunks.get(0).text().length());
        assertEquals(1500, chunks.get(1).text().length());
        assertEquals(200, chunks.get(2).text().length());
    }

    @Test
    void shortTextBeforeALongParagraphGetsItsOwnChunk() {
        String text = "Intro paragraph.\n\n" + "y".repeat(2000) + "\n\nOutro paragraph.";

        List<TextChunk> chunks = service.chunk(List.of(page(1, text)));

        assertEquals(4, chunks.size());
        assertEquals("Intro paragraph.", chunks.get(0).text());
        assertEquals("Outro paragraph.", chunks.get(3).text());
    }
}
