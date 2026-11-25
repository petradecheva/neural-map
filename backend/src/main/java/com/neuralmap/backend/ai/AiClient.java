package com.neuralmap.backend.ai;

import java.util.Map;

public interface AiClient {
    String generate(String prompt, Map<String, Object> schema);
}
