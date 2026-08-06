package com.jrouter.config;

import com.jrouter.core.LLMOrchestrator;
import com.jrouter.provider.AIProvider;
import com.jrouter.provider.GeminiProvider;
import com.jrouter.provider.GroqProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public LLMOrchestrator llmOrchestrator() {
        AIProvider gemini = new GeminiProvider(List.of("gemini-key-001", "gemini-key-002"));
        AIProvider groq = new GroqProvider(List.of("groq-key-001"));

        return new LLMOrchestrator(List.of(gemini, groq));
    }
}