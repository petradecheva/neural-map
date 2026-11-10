package com.neuralmap.backend.document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.neuralmap.backend.exception.DocumentReadException;
import com.neuralmap.backend.exception.InvalidDocumentException;

@Service
public class TextExtractionService {

    private final Path uploadDir;
    private static final int MIN_CHARS = 20;
    public TextExtractionService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }
    
    public List<PageText> extract(UUID id)   {
        List<PageText> list = new ArrayList<>();
        Path filePath = uploadDir.resolve(id+".pdf");
        try (PDDocument document = Loader.loadPDF(filePath.toFile())){
            PDFTextStripper textStripper = new PDFTextStripper();
            int numberOfPages = document.getNumberOfPages();
            int counter = 0;
            for(int p = 1; p<=numberOfPages; p++){
                textStripper.setStartPage(p);
                textStripper.setEndPage(p);
                String text = textStripper.getText(document);
                int length = text.strip().length();
                boolean lowText = false;
                if(length < MIN_CHARS) {
                    lowText = true; 
                }
                else counter++;
                list.add(new PageText(p, text, lowText));
            }
            if(counter == 0){
                throw new InvalidDocumentException("This PDF has no readable text");
            }
        } catch (IOException e){
            throw new DocumentReadException("There is a problem reading the document.", e);
        }
        return  list;
        
    }
}
