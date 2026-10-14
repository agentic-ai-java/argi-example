/*
 * Copyright 2026 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy at https://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package io.github.agentic.ai.examples;

import io.github.agentic.ai.adapter.agentscope.AgentScopeChatClientAdapter;
import io.github.agentic.ai.api.chat.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** Demonstrates native model construction and the neutral ARGI invocation boundary. */
public final class AgentScopeExample {
    private AgentScopeExample() {}

    public static ChatClient client(Map<String, String> environment) {
        String key = environment.get("ARGI_API_KEY");
        String model = environment.get("ARGI_MODEL");
        if (key == null || key.isBlank() || model == null || model.isBlank()) {
            throw new IllegalArgumentException("Set ARGI_API_KEY and ARGI_MODEL before running this example");
        }
        String baseUrl = environment.getOrDefault("ARGI_BASE_URL", "https://api.openai.com/v1");
        return new AgentScopeChatClientAdapter(io.agentscope.extensions.model.openai.OpenAIChatModel.builder().apiKey(key).modelName(model).baseUrl(baseUrl).stream(false).generateOptions(io.agentscope.core.model.GenerateOptions.builder().executionConfig(io.agentscope.core.model.ExecutionConfig.builder().timeout(Duration.ofSeconds(60)).maxAttempts(1).build()).build()).build());
    }
    public static String ask(ChatClient client, String question) {
        return client.callBlocking(ChatRequest.of(List.of(ChatMessage.user(question)))).text();
    }
    public static void main(String[] args) {
        org.slf4j.LoggerFactory.getLogger(AgentScopeExample.class).info("{}", ask(client(System.getenv()), "Explain checkpointing in one sentence."));
    }
}
