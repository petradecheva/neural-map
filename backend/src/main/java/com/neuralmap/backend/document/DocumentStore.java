package com.neuralmap.backend.document;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

@Repository
public class DocumentStore {
    private final Map<UUID, Document> documents = new ConcurrentHashMap<>();
    public void save (Document doc){
        documents.put(doc.id(), doc);
    }

    public Optional<Document> find(UUID id){
        return Optional.ofNullable(documents.get(id));
    }

}
