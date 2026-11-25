package com.neuralmap.backend.ai;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service 
public class OllamaClient implements AiClient {
    private final RestClient restClient;
    private final String model;
    private final int contextSize;
    public OllamaClient(@Value("${app.ollama.base-url}") String baseUrl, 
                        @Value("${app.ollama.model}") String model, 
                        @Value("${app.ollama.timeout-seconds}") int timeoutSeconds,
                        @Value("${app.ollama.context-size}") int contextSize){
        this.model = model;
        this.contextSize = contextSize;                    
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(timeoutSeconds));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();

        
    }

    @Override
    public String generate(String prompt, Map<String, Object> schema) {
        Map<String, Object> requestBody = Map.of(
                                "model", model,
                                "stream", false,
                                "think", false,
                                "messages", List.of(Map.of("role", "user", "content", prompt)),
                                "format", schema,
                                "options", Map.of("num_ctx", contextSize)
                                );
        try {
            Map<?, ?> response = restClient.post()
                                        .uri("/api/chat")
                                        .body(requestBody)
                                        .retrieve()
                                        .body(Map.class);
            
            if(response == null || !(response.get("message") instanceof Map<?, ?> message) || !(message.get("content") instanceof String content)){
                throw new AiException("The AI model sent an unexpected answer.", null);
            }
        
            return content;
        } catch(RestClientException e) {
            throw new AiException("The AI model is not available.", e);
        }
    }

}