package com.jrouter.provider;

import com.jrouter.model.PromptRequest;
import com.jrouter.model.AIResponse;

import java.util.List;

public class GeminiProvider implements AIProvider {
    private final List<String> apiKeys;
    private int currentKeyIndex = 0;
    private String forceFailOnModel = null;

    private static final String PRIMARY_MODEL = "gemini-1.5-pro";
    private static final String BACKUP_MODEL = "gemini-1.5-flash";

    public GeminiProvider(List<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    public void setForceFailOnModel(String modelName) {
        this.forceFailOnModel = modelName;
    }

    @Override
    public AIResponse execute(PromptRequest request) throws Exception {
        if (apiKeys == null || apiKeys.isEmpty()) {
            throw new IllegalStateException("No Gemini API keys configured.");
        }

        // Layer 1: try all keys against the primary model
        AIResponse result = tryAllKeys(request, PRIMARY_MODEL);
        if (result != null) return result;

        System.out.println("[Gemini] All keys failed on " + PRIMARY_MODEL + ". Swapping to backup model: " + BACKUP_MODEL);

        // Layer 2: try all keys again against the backup model
        result = tryAllKeys(request, BACKUP_MODEL);
        if (result != null) return result;

        throw new RuntimeException("All Gemini keys exhausted on both " + PRIMARY_MODEL + " and " + BACKUP_MODEL + ".");
    }

    private AIResponse tryAllKeys(PromptRequest request, String modelName) {
        for (int attempt = 0; attempt < apiKeys.size(); attempt++) {
            String key = apiKeys.get(currentKeyIndex);
            long startTime = System.currentTimeMillis();

            try {
                if (key == null || key.isEmpty()) {
                    throw new IllegalStateException("Key #" + currentKeyIndex + " is missing or invalid.");
                }
                if (modelName.equals(forceFailOnModel)) {
                    throw new RuntimeException("429 Too Many Requests - " + modelName + " overloaded");
                }

                long duration = System.currentTimeMillis() - startTime;
                return new AIResponse(
                        "Response from Gemini (" + modelName + ") for: " + request.prompt(),
                        getProviderName(),
                        modelName,
                        duration,
                        true
                );
            } catch (Exception e) {
                System.out.println("[Gemini] Key #" + currentKeyIndex + " failed on " + modelName + ": " + e.getMessage());
                currentKeyIndex = (currentKeyIndex + 1) % apiKeys.size();
            }
        }
        return null;
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