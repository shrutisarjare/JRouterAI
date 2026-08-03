package com.jrouter.model;

public record PromptRequest(
        String prompt,
        String preferredModel,
        double temperature
) {
    public PromptRequest(String prompt) {
        this(prompt, "default", 0.7);
    }
}