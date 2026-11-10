package com.neuralmap.backend.document;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService docService;
   
    public DocumentController(DocumentService docService) {
        this.docService = docService;
        
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadResponse upload(@RequestParam("file") MultipartFile file){
        Document doc = docService.upload(file);
       return new UploadResponse(doc.id(), doc.fileName(), doc.sizeBytes(), doc.pages().size());
    }

    @GetMapping("/{id}/pages")
    public List<PageText> pages(@PathVariable UUID id){
        return docService.get(id).pages();
    }

}
