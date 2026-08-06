package com.jrouter.controller;

import com.jrouter.core.LLMOrchestrator;
import com.jrouter.model.AIResponse;
import com.jrouter.model.PromptRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final LLMOrchestrator orchestrator;

    public ChatController(LLMOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/v1/chat")
    public AIResponse chat(@RequestBody PromptRequest request) throws Exception {
        return orchestrator.routeAndExecute(request);
    }
}