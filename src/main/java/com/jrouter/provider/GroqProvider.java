package com.jrouter.provider;

import com.jrouter.model.PromptRequest;
import com.jrouter.model.AIResponse;

import java.util.List;

public class GroqProvider implements AIProvider {
    private final List<String> apiKeys;
    private int currentKeyIndex = 0;
    private boolean simulateFailure = false;

    public GroqProvider(List<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    @Override
    public AIResponse execute(PromptRequest request) throws Exception {
        if (apiKeys == null || apiKeys.isEmpty()) {
            throw new IllegalStateException("No Groq API keys configured.");
        }

        Exception lastError = null;

        for (int attempt = 0; attempt < apiKeys.size(); attempt++) {
            String key = apiKeys.get(currentKeyIndex);
            long startTime = System.currentTimeMillis();

            try {
                if (key == null || key.isEmpty()) {
                    throw new IllegalStateException("Key #" + currentKeyIndex + " is missing or invalid.");
                }
                if (simulateFailure) {
                    throw new RuntimeException("429 Too Many Requests - key #" + currentKeyIndex + " rate limited");
                }

                long duration = System.currentTimeMillis() - startTime;
                return new AIResponse(
                        "Response from Groq for: " + request.prompt(),
                        getProviderName(),
                        "llama3-8b-8192",
                        duration,
                        true
                );
            } catch (Exception e) {
                System.out.println("[Groq] Key #" + currentKeyIndex + " failed: " + e.getMessage());
                lastError = e;
                currentKeyIndex = (currentKeyIndex + 1) % apiKeys.size();
            }
        }

        throw new RuntimeException(
                "All Groq keys exhausted. Last error: " +
                        (lastError != null ? lastError.getMessage() : "unknown")
        );
    }

    @Override
    public String getProviderName() {
        return "GROQ";
    }

    @Override
    public boolean isAvailable() {
        return apiKeys != null && !apiKeys.isEmpty();
    }
}