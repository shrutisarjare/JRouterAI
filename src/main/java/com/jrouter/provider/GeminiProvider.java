package com.jrouter.provider;

import com.jrouter.model.PromptRequest;
import com.jrouter.model.AIResponse;

import java.util.List;

public class GeminiProvider implements AIProvider {
    private final List<String> apiKeys;
    private int currentKeyIndex = 0;
    private boolean simulateFailure = false;

    public GeminiProvider(List<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    @Override
    public AIResponse execute(PromptRequest request) throws Exception {
        if (apiKeys == null || apiKeys.isEmpty()) {
            throw new IllegalStateException("No Gemini API keys configured.");
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
                        "Response from Gemini for: " + request.prompt(),
                        getProviderName(),
                        "gemini-1.5-flash",
                        duration,
                        true
                );
            } catch (Exception e) {
                System.out.println("[Gemini] Key #" + currentKeyIndex + " failed: " + e.getMessage());
                lastError = e;
                currentKeyIndex = (currentKeyIndex + 1) % apiKeys.size(); // sticky: move on and stay there
            }
        }

        throw new RuntimeException(
                "All Gemini keys exhausted. Last error: " +
                        (lastError != null ? lastError.getMessage() : "unknown")
        );
    }

    @Override
    public String getProviderName() {
        return "GEMINI";
    }

    @Override
    public boolean isAvailable() {
        return apiKeys != null && !apiKeys.isEmpty();
    }
}