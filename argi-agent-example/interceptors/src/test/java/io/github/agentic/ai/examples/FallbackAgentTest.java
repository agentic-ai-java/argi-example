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

import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.graph.agent.interceptor.ModelRequest;
import io.github.agentic.ai.graph.agent.interceptor.ModelResponse;
import io.github.agentic.ai.graph.agent.interceptor.modelfallback.ModelFallbackInterceptor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;


class FallbackAgentTest {
    @Test
    void preservesConversationAndSwitchesOnlyAfterFailure() {
        var primary = mock(ChatClient.class);
        var fallback = mock(ChatClient.class);
        var messages = List.of(ChatMessage.user("Resume my workflow"));
        var request = ModelRequest.builder().chatClient(primary).messages(messages).build();
        var interceptor = ModelFallbackInterceptor.builder().addFallbackClient(fallback).build();
        var answer = interceptor.interceptModel(request, current -> {
            assertEquals(messages, current.getMessages());
            if (current.getChatClient() == primary) {
                throw new IllegalStateException("Injected primary outage");
            }
            assertSame(fallback, current.getChatClient());
            return ModelResponse.of(ChatMessage.assistant("Use the saved checkpoint"));
        });
        assertEquals("Use the saved checkpoint", answer.getMessage().text());
        assertThrows(CancellationException.class, () -> interceptor.interceptModel(request, current -> {
            throw new CancellationException("Caller stopped");
        }));
    }

    @Test
    void alternateConfigurationDoesNotMutatePrimaryCredentials() {
        var environment = Map.of("ARGI_MODEL", "primary", "ARGI_API_KEY", "primary-fixture",
            "ARGI_FALLBACK_MODEL", "fallback", "ARGI_FALLBACK_API_KEY", "fallback-fixture");
        var alternate = FallbackAgent.fallbackEnvironment(environment);
        assertEquals("fallback", alternate.get("ARGI_MODEL"));
        assertEquals("fallback-fixture", alternate.get("ARGI_API_KEY"));
        assertEquals("primary", environment.get("ARGI_MODEL"));
        assertThrows(IllegalArgumentException.class, () -> FallbackAgent.fallbackEnvironment(Map.of()));
    }
}
