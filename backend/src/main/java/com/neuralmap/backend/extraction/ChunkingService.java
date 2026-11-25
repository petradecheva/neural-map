package com.neuralmap.backend.extraction;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.neuralmap.backend.document.PageText;

@Service
public class ChunkingService{
    private static final int MAX_CHARS = 1500;

    public List<TextChunk> chunk(List<PageText> pages){
        
        List<TextChunk> chunks = new ArrayList<>();
        for(PageText page : pages){
            if(page.lowText()) continue;

            StringBuilder current = new StringBuilder();
            for(String paragraph : page.text().split("\\n\\s*\\n")) {
                String p = paragraph.strip();
                if(p.isEmpty()){
                    continue;
                }

                if(p.length() > MAX_CHARS) {
                    flush(current, page.pageNumber(), chunks);
                    for(String piece : cut(p)){
                        chunks.add(new TextChunk(page.pageNumber(), piece));
                    }
                    continue;
                }

                if(current.length() + p.length() + 2 > MAX_CHARS) {
                    flush(current, page.pageNumber(), chunks);
                }

                if(current.length()>0){
                    current.append("\n\n");
                }
                current.append(p);
            }
            
            flush(current, page.pageNumber(), chunks);
        }
        return chunks;
    }

    private void flush(StringBuilder current, int pageNumber, List<TextChunk> chunks) {
        if(current.length() > 0){
            chunks.add(new TextChunk(pageNumber, current.toString()));
            current.setLength(0);
        }
    }

    private List<String> cut(String text){
        List<String> pieces = new ArrayList<>();
        String rest = text;
        while (rest.length() > MAX_CHARS){
            int end = rest.lastIndexOf(' ', MAX_CHARS);
            if(end <=0){
                end = MAX_CHARS;
            }
            pieces.add(rest.substring(0, end).strip());
            rest = rest.substring(end).strip();
        }
        if(!rest.isEmpty()){
            pieces.add(rest);
        }
        return pieces;
    }

}