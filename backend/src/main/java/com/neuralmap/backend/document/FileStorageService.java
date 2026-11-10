package com.neuralmap.backend.document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neuralmap.backend.exception.DocumentStorageException;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }
    
    public UUID save(MultipartFile file)   {
        UUID id = UUID.randomUUID();
        try (InputStream content = file.getInputStream()){
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(id + ".pdf");
            Files.copy(content, filePath);
        } catch (IOException e){
            throw new DocumentStorageException("There is a problem saving the document.", e);
        }
        return id;
        
    }
    public void delete(UUID id){
        try {
            Files.deleteIfExists(uploadDir.resolve(id+".pdf"));
        } catch (IOException ex) {
           throw new DocumentStorageException("There is a problem deleting the document.", ex);
        }
    }
}
