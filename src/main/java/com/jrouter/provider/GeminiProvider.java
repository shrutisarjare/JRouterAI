package com.jrouter.provider;

import com.jrouter.model.PromptRequest;
import com.jrouter.model.AIResponse;

public class GeminiProvider implements AIProvider {
    private final String apiKey;
    private boolean simulateFailure = false;

    public GeminiProvider(String apiKey) {
        this.apiKey = apiKey;
    }
    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    @Override
    public AIResponse execute(PromptRequest request) throws Exception {
        long startTime = System.currentTimeMillis();

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key is missing or invalid.");
        }
        if (simulateFailure) {
            throw new RuntimeException("429 Too Many Requests - Gemini rate limited");
        }

        long duration = System.currentTimeMillis() - startTime;
        return new AIResponse(
                "Response from Gemini for: " + request.prompt(),
                getProviderName(),
                "gemini-1.5-flash",
                duration,
                true
        );
    }

    @Override
    public String getProviderName() {
        return "GEMINI";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}