package com.jrouter.core;

import com.jrouter.model.PromptRequest;
import com.jrouter.model.AIResponse;
import com.jrouter.provider.AIProvider;
import com.jrouter.provider.GroqProvider;
import com.jrouter.provider.GeminiProvider;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== J-RouterAI Failover Simulation ===");

        PromptRequest request = new PromptRequest("What is the difference between a process and a thread?");

        // Groq key is empty (will fail), Gemini key is valid (will succeed)
        AIProvider brokenGroq = new GroqProvider("");
        AIProvider healthyGemini = new GeminiProvider("gemini-actual-api-key-999");

        LLMOrchestrator orchestrator = new LLMOrchestrator(List.of(brokenGroq, healthyGemini));

        try {
            AIResponse response = orchestrator.routeAndExecute(request);
            System.out.println("\n=== Final Successful Result ===");
            System.out.println("Output: " + response.content());
            System.out.println("Served By: " + response.providerName());
            System.out.println("Latency: " + response.executionTimeMs() + "ms");
        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
        }
    }
}