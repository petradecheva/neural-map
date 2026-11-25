package com.neuralmap.backend.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.neuralmap.backend.document.PageText;

class ChunkingServiceTest {

    private static final int MAX_CHARS = ChunkingService.MAX_CHARS;

    private final ChunkingService service = new ChunkingService();

    private static PageText page(int number, String text) {
        return new PageText(number, text, false);
    }

    // n paragraphs of `size` chars each ("aaa...", "bbb...", ...), separated by blank lines
    private static String paragraphs(int n, int size) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(String.valueOf((char) ('a' + i)).repeat(size));
        }
        return String.join("\n\n", list);
    }

    @Test
    void shortPageBecomesOneChunk() {
        List<TextChunk> chunks = service.chunk(List.of(page(1, "Cells are the basic unit of life.")));

        assertEquals(1, chunks.size());
        assertEquals(1, chunks.get(0).startPage());
        assertEquals(1, chunks.get(0).endPage());
        assertEquals("Cells are the basic unit of life.", chunks.get(0).text());
    }

    @Test
    void emptyInputGivesNoChunks() {
        assertTrue(service.chunk(List.of()).isEmpty());
        assertTrue(service.chunk(List.of(page(1, "  \n\n   \n"))).isEmpty());
    }

    @Test
    void shortPagesAreMergedIntoOneChunk() {
        // like presentation slides: little text per page
        List<TextChunk> chunks = service.chunk(List.of(
                page(1, "Slide one about requirements."),
                page(2, "Slide two about user stories."),
                page(3, "Slide three about testing.")));

        assertEquals(1, chunks.size());
        assertEquals(1, chunks.get(0).startPage());
        assertEquals(3, chunks.get(0).endPage());
        assertEquals("Slide one about requirements.\n\nSlide two about user stories.\n\nSlide three about testing.",
                chunks.get(0).text());
    }

    @Test
    void lowTextPagesAreSkippedButNeighboursStillMerge() {
        List<TextChunk> chunks = service.chunk(List.of(
                page(1, "First page with readable text."),
                new PageText(2, "Logo", true),
                page(3, "Third page with readable text.")));

        assertEquals(1, chunks.size());
        assertEquals(1, chunks.get(0).startPage());
        assertEquals(3, chunks.get(0).endPage());
        assertFalse(chunks.get(0).text().contains("Logo"));
    }

    @Test
    void newChunkStartsWhenTheNextPageDoesNotFit() {
        // each page is a third of the limit: pages 1 and 2 fit together, page 3 does not
        String third = "x".repeat(MAX_CHARS / 3);

        List<TextChunk> chunks = service.chunk(List.of(page(1, third), page(2, third), page(3, third)));

        assertEquals(2, chunks.size());
        assertEquals(1, chunks.get(0).startPage());
        assertEquals(2, chunks.get(0).endPage());
        assertEquals(3, chunks.get(1).startPage());
        assertEquals(3, chunks.get(1).endPage());
    }

    @Test
    void paragraphsAreGroupedUntilTheLimit() {
        // paragraphs of a quarter of the limit: 3 fit in one chunk (3 * size + 4), the 4th does not
        String text = paragraphs(5, MAX_CHARS / 4);

        List<TextChunk> chunks = service.chunk(List.of(page(3, text)));

        assertEquals(2, chunks.size());
        for (TextChunk chunk : chunks) {
            assertTrue(chunk.text().length() <= MAX_CHARS);
            assertEquals(3, chunk.startPage());
            assertEquals(3, chunk.endPage());
        }
        // nothing lost and the order is kept
        assertEquals(text, chunks.get(0).text() + "\n\n" + chunks.get(1).text());
    }

    @Test
    void longParagraphIsCutAtSpacesWithoutLosingWords() {
        // one paragraph of about 2.5 * MAX_CHARS with no blank lines, like many PDFs
        StringBuilder sb = new StringBuilder();
        for (int i = 0; sb.length() < MAX_CHARS * 2 + MAX_CHARS / 2; i++) {
            sb.append("word").append(i).append(' ');
        }
        String text = sb.toString().strip();

        List<TextChunk> chunks = service.chunk(List.of(page(1, text)));

        assertTrue(chunks.size() >= 3);
        List<String> wordsInChunks = new ArrayList<>();
        for (TextChunk chunk : chunks) {
            assertTrue(chunk.text().length() <= MAX_CHARS);
            assertEquals(1, chunk.startPage());
            wordsInChunks.addAll(Arrays.asList(chunk.text().split(" ")));
        }
        // every word is still there, whole and in order (this catches a lost last piece)
        assertEquals(Arrays.asList(text.split(" ")), wordsInChunks);
    }

    @Test
    void textWithoutSpacesIsCutAtTheLimit() {
        String text = "x".repeat(MAX_CHARS * 2 + 200);

        List<TextChunk> chunks = service.chunk(List.of(page(1, text)));

        assertEquals(3, chunks.size());
        assertEquals(MAX_CHARS, chunks.get(0).text().length());
        assertEquals(MAX_CHARS, chunks.get(1).text().length());
        assertEquals(200, chunks.get(2).text().length());
    }

    @Test
    void longParagraphOnALaterPageKeepsEarlierTextSeparate() {
        // page 1 is short, page 2 is one huge paragraph, page 3 is short
        List<TextChunk> chunks = service.chunk(List.of(
                page(1, "Intro paragraph."),
                page(2, "y".repeat(MAX_CHARS + 500)),
                page(3, "Outro paragraph.")));

        assertEquals(4, chunks.size());
        assertEquals("Intro paragraph.", chunks.get(0).text());
        assertEquals(1, chunks.get(0).endPage());
        assertEquals(2, chunks.get(1).startPage());
        assertEquals(2, chunks.get(2).endPage());
        assertEquals("Outro paragraph.", chunks.get(3).text());
        assertEquals(3, chunks.get(3).startPage());
    }
}
