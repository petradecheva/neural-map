package com.neuralmap.backend.document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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

    public List<UUID> idList(){
        if(!Files.exists(uploadDir)) return List.of();
        try(Stream<Path> files = Files.list(uploadDir)){
            List<UUID> ids = new ArrayList<>();
            for(Path file : files.toList()){
                String name = file.getFileName().toString();
                if(!name.endsWith(".pdf")){
                    continue;
                }
                String idText = name.substring(0, name.length()-4);
                try {
                    ids.add(UUID.fromString(idText));
                } catch (IllegalArgumentException e) {
                    // not one of our files, ignore it
                }
            }
            return ids;
        } catch(IOException e){
            throw new DocumentStorageException("There is a problem reading the upload folder", e);
        }
    }
}
