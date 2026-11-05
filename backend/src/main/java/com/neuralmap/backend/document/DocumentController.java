package com.neuralmap.backend.document;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    // Step 2: only receive the file and report its name and size.
    // Validation, storage and text extraction come in the next steps.
    private final DocumentService docService;
    public DocumentController(DocumentService docService) {
        this.docService = docService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadResponse upload(@RequestParam("file") MultipartFile file){
        docService.validate(file);
       return new UploadResponse(file.getOriginalFilename(), file.getSize());
    }
}
