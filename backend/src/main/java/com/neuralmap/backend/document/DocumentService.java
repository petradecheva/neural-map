package com.neuralmap.backend.document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neuralmap.backend.exception.DocumentNotFoundException;
import com.neuralmap.backend.exception.InvalidDocumentException;
@Service
public class DocumentService {
    private final FileStorageService fileService;
    private final TextExtractionService extractionService;
    private final DocumentStore docStore;

    public DocumentService(FileStorageService fileService, TextExtractionService extractionService, DocumentStore docStore){
        this.fileService = fileService;
        this.extractionService = extractionService;
        this.docStore = docStore;
    }

    public void validate(MultipartFile file)  {
        if (file.isEmpty()) {
                throw new InvalidDocumentException("The file is empty.");
        }
        // check the real PDF header: the first bytes are "%PDF-"
        try (InputStream content = file.getInputStream()) {
            byte[] head = content.readNBytes(5);
            String headString = new String(head, StandardCharsets.US_ASCII);
            if(!headString.equals("%PDF-")) {
                throw new InvalidDocumentException("The file is not a valid PDF.");
            }
        } catch (IOException e){
            throw new InvalidDocumentException("There is a problem reading the file.");
        }
        
    }

    public Document upload(MultipartFile file){
        validate(file);
        List<PageText> list;
        UUID id = fileService.save(file);
        try{
            list = extractionService.extract(id);
            Document doc = new Document(id, file.getOriginalFilename(), file.getSize(), list);
            docStore.save(doc);
            return doc;
        } catch(RuntimeException e){
            fileService.delete(id);
            throw e;
        }
        
    }

    public Document get(UUID id){
        Document doc = docStore.find(id).orElseThrow(()-> new DocumentNotFoundException("The Document cannot be found in the storage"));
        return doc;
    }
}
