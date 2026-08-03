package com.jrouter.provider;

import com.jrouter.model.PromptRequest;
import com.jrouter.model.AIResponse;

public interface AIProvider {
    AIResponse execute(PromptRequest request) throws Exception;
    String getProviderName();
    boolean isAvailable();
}