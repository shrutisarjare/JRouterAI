package com.jrouter.core;

import com.jrouter.model.PromptRequest;
import com.jrouter.model.AIResponse;
import com.jrouter.provider.AIProvider;
import com.jrouter.provider.GroqProvider;
import com.jrouter.provider.GeminiProvider;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== J-RouterAI Key Pool Failover Simulation ===");

        PromptRequest request = new PromptRequest("What is the difference between a process and a thread?");

        // Gemini: first key is broken (empty), second key is valid
        AIProvider gemini = new GeminiProvider(List.of("", "gemini-real-key-002"));
        AIProvider groq = new GroqProvider(List.of("groq-real-key-001"));

        LLMOrchestrator orchestrator = new LLMOrchestrator(List.of(gemini, groq));

        try {
            AIResponse response = orchestrator.routeAndExecute(request);
            System.out.println("\n=== Final Successful Result ===");
            System.out.println("Output: " + response.content());
            System.out.println("Served By: " + response.providerName());
            System.out.println("Model Used: " + response.modelUsed());
            System.out.println("Latency: " + response.executionTimeMs() + "ms");
        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
        }
    }
}