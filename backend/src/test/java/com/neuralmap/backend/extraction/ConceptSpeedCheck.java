package com.neuralmap.backend.extraction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import com.neuralmap.backend.ai.OllamaClient;
import com.neuralmap.backend.document.PageText;
import com.neuralmap.backend.document.TextExtractionService;

/**
 * Manual speed test for step 2 of the dev plan. Needs Ollama running. Skipped in normal test runs.
 *
 * Run from the backend folder:
 *   RUN_SPEED_TEST=true ./gradlew test --tests '*ConceptSpeedCheck' --rerun -i
 * Optional: SPEED_TEST_PDF=path/to/file.pdf (default: uploads/lectures/SEBA01.pdf)
 */
@EnabledIfEnvironmentVariable(named = "RUN_SPEED_TEST", matches = "true")
class ConceptSpeedCheck {

    @TempDir
    Path tempDir;

    // First draft of the concept prompt (step 3 will refine it).
    private static final String PROMPT = """
            You extract the key concepts from a piece of lecture text for a study knowledge graph.
            List at most 5 concepts: only the most important ones that the text actually explains.
            Do not invent anything. Ignore headers, footers, page numbers and "Downloaded by" lines.
            For each concept give:
            - name: a short name (1 to 4 words)
            - definition: at most 20 words, based only on the text
            - category: one word, for example term, principle, method, process, tool, role, artifact
            - evidence: at most 15 words copied exactly from the text
            Keep the language of the text.

            Text from pages %d to %d:
            \"\"\"
            %s
            \"\"\"
            """;

    private static final Map<String, Object> SCHEMA = Map.of(
            "type", "object",
            "properties", Map.of("concepts", Map.of(
                    "type", "array",
                    "maxItems", 5,
                    "items", Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "name", Map.of("type", "string"),
                                    "definition", Map.of("type", "string"),
                                    "category", Map.of("type", "string"),
                                    "evidence", Map.of("type", "string")),
                            "required", List.of("name", "definition", "category", "evidence")))),
            "required", List.of("concepts"));

    @Test
    void measureConceptExtraction() throws Exception {
        Path pdf = Path.of(System.getenv().getOrDefault("SPEED_TEST_PDF", "uploads/lectures/SEBA01.pdf"));

        // TextExtractionService reads <id>.pdf from its folder, so copy the PDF there.
        UUID id = UUID.randomUUID();
        Files.copy(pdf, tempDir.resolve(id + ".pdf"));
        List<PageText> pages = new TextExtractionService(tempDir.toString()).extract(id);
        List<TextChunk> chunks = new ChunkingService().chunk(pages);

        long lowText = pages.stream().filter(PageText::lowText).count();
        int totalChars = chunks.stream().mapToInt(c -> c.text().length()).sum();
        report("pages: %d (low text: %d), chunks: %d, avg chunk: %d chars",
                pages.size(), lowText, chunks.size(), chunks.isEmpty() ? 0 : totalChars / chunks.size());

        // Measure a typical chunk (median length) and the longest one.
        List<TextChunk> sorted = new ArrayList<>(chunks);
        sorted.sort(Comparator.comparingInt(c -> c.text().length()));
        List<TextChunk> samples = List.of(sorted.get(sorted.size() / 2), sorted.get(sorted.size() - 1));

        OllamaClient ai = new OllamaClient("http://localhost:11434", "qwen3.5:4b", 600, 8192);
        long totalMs = 0;
        for (TextChunk chunk : samples) {
            long start = System.currentTimeMillis();
            String json = ai.generate(PROMPT.formatted(chunk.startPage(), chunk.endPage(), chunk.text()), SCHEMA);
            long ms = System.currentTimeMillis() - start;
            totalMs += ms;

            report("---- pages %d-%d, %d chars, took %.1f s", chunk.startPage(), chunk.endPage(), chunk.text().length(), ms / 1000.0);
            report("chunk text: %s", chunk.text().replace("\n", " "));
            report("model output: %s", json.replace("\n", " "));
        }

        double avgSec = totalMs / 1000.0 / samples.size();
        report("==== avg per chunk: %.1f s -> concepts for all %d chunks: ~%.0f min, with relationship pass: ~%.0f min",
                avgSec, chunks.size(), avgSec * chunks.size() / 60, 2 * avgSec * chunks.size() / 60);
    }

    private static void report(String format, Object... args) {
        System.out.println("SPEED| " + format.formatted(args));
    }
}
