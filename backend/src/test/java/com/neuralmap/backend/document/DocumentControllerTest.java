package com.neuralmap.backend.document;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.neuralmap.backend.support.PdfTestUtil;

// Starts the whole app and sends fake HTTP requests to it (no real server port needed).
@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerTest {

    // static, because Spring reads the property before the test object is created
    @TempDir
    static Path uploadDir;

    // Point the app at the temp folder so tests never touch the real "uploads" folder.
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> uploadDir.toString());
    }

    @Autowired
    MockMvc mvc;

    private MockMultipartFile file(byte[] content) {
        return new MockMultipartFile("file", "lecture.pdf", "application/pdf", content);
    }

    @Test
    void uploadReturns201WithDocumentInfo() throws Exception {
        mvc.perform(multipart("/api/documents").file(file(PdfTestUtil.pdfBytes("Page one has enough text.", "Page two also has enough text."))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fileName").value("lecture.pdf"))
                .andExpect(jsonPath("$.pageCount").value(2));
    }

    @Test
    void uploadOfNonPdfReturns400() throws Exception {
        mvc.perform(multipart("/api/documents").file(file("not a pdf".getBytes())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The file is not a valid PDF."));
    }

    @Test
    void uploadOfEmptyFileReturns400() throws Exception {
        mvc.perform(multipart("/api/documents").file(file(new byte[0])))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadWithoutFileReturns400() throws Exception {
        mvc.perform(multipart("/api/documents"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void pagesReturnsExtractedTextOfUploadedDocument() throws Exception {
        String response = mvc.perform(multipart("/api/documents").file(file(PdfTestUtil.pdfBytes("Enzymes speed up reactions.", null))))
                .andReturn().getResponse().getContentAsString();
        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mvc.perform(get("/api/documents/{id}/pages", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].pageNumber").value(1))
                .andExpect(jsonPath("$[0].lowText").value(false))
                .andExpect(jsonPath("$[1].lowText").value(true));
    }

    @Test
    void pagesOfUnknownIdReturns404() throws Exception {
        mvc.perform(get("/api/documents/{id}/pages", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
