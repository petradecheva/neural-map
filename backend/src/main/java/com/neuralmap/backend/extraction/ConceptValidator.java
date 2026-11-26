package com.neuralmap.backend.extraction;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.neuralmap.backend.document.PageText;
@Component
public class ConceptValidator { 
    
    public Optional<Concept> validate(RawConcept raw, TextChunk chunk, List<PageText> pages) {
        String rawName = raw.name().strip();
        String rawDefinition = raw.definition().strip();
        if(raw == null || raw.name() == null || raw.definition() == null || raw.definition().isBlank()|| raw.name().isBlank()){
            return Optional.empty();
        }
        String id = toId(rawName);
        if(id.isEmpty()) {
            return Optional.empty();
        }
        String evidence = raw.evidence() == null ? null
                : raw.evidence().strip().replaceAll("^[\"'“”„]+|[\"'“”„]+$", "");
        int page = findEvidencePage(evidence, chunk, pages);
        ConceptCategory category = parseCategory(raw.category());
        if( page!=-1) return Optional.of(new Concept(id, rawName, rawDefinition, category, evidence, page, page));
        else return Optional.of(new Concept(id, rawName, rawDefinition, category, null, chunk.startPage(), chunk.endPage()));

    }

    private ConceptCategory parseCategory(String text){
        if(text == null){
            return ConceptCategory.OTHER;
        }
        try {
            return ConceptCategory.valueOf(text.strip().toUpperCase());
        } catch(IllegalArgumentException ex){
            return ConceptCategory.OTHER;
        }
    }

    private String toId(String name){
    
        return name.toLowerCase().replaceAll("[^\\p{L}\\p{N}]+", "-").replaceAll("^-+|-+$", "");
    }

    private int findEvidencePage(String evidence, TextChunk chunk, List<PageText> pages){
        if(evidence == null || evidence.isBlank()) return -1;
        if(evidence.contains("...") || evidence.contains("…")) return -1;
        if(evidence.split("\\s+").length <3) return -1;
        evidence = normalize(evidence);
        for (PageText page : pages) {
        if (page.pageNumber() >= chunk.startPage() && page.pageNumber() <= chunk.endPage()
                && normalize(page.text()).contains(evidence)) {
            return page.pageNumber();
        }
}
        return -1;
    }

    private String normalize(String text){
        return text.toLowerCase().replaceAll("\\s+", " ").strip();
    }
}
