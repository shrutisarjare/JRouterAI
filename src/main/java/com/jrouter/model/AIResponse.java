package com.jrouter.model;

public record AIResponse(
        String content,
        String providerName,
        String modelUsed,
        long executionTimeMs,
        boolean success
) {}