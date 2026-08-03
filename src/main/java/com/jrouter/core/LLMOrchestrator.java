package com.jrouter.core;

import com.jrouter.model.PromptRequest;
import com.jrouter.model.AIResponse;
import com.jrouter.provider.AIProvider;

import java.util.ArrayList;
import java.util.List;

public class LLMOrchestrator {
    private final List<AIProvider> providers;

    public LLMOrchestrator(List<AIProvider> providers) {
        this.providers = new ArrayList<>(providers);
    }

    public AIResponse routeAndExecute(PromptRequest request) throws Exception {
        List<String> failureLogs = new ArrayList<>();

        for (AIProvider provider : providers) {
            if (!provider.isAvailable()) {
                failureLogs.add(provider.getProviderName() + ": Unavailable (missing or empty key).");
                continue;
            }

            try {
                System.out.println("[Orchestrator] Attempting execution with provider: " + provider.getProviderName());
                AIResponse response = provider.execute(request);
                System.out.println("[Orchestrator] Request succeeded using: " + provider.getProviderName());
                return response;
            } catch (Exception e) {
                String logEntry = provider.getProviderName() + " failed: " + e.getMessage();
                System.err.println("[Orchestrator Failover] " + logEntry);
                failureLogs.add(logEntry);
            }
        }

        throw new RuntimeException("All configured AI providers failed:\n" + String.join("\n", failureLogs));
    }
}