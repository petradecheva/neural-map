package com.neuralmap.backend.document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neuralmap.backend.exception.InvalidDocumentException;
@Service
public class DocumentService {
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
}
