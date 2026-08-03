package com.jrouter.provider;

import com.jrouter.model.PromptRequest;
import com.jrouter.model.AIResponse;

public class GroqProvider implements AIProvider {
    private final String apiKey;

    public GroqProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public AIResponse execute(PromptRequest request) throws Exception {
        long startTime = System.currentTimeMillis();

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Groq API key is missing or invalid.");
        }

        long duration = System.currentTimeMillis() - startTime;
        return new AIResponse(
                "Response from Groq for: " + request.prompt(),
                getProviderName(),
                "llama3-8b-8192",
                duration,
                true
        );
    }

    @Override
    public String getProviderName() {
        return "GROQ";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}