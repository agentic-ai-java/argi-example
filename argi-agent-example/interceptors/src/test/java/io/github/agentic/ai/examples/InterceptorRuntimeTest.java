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
import io.github.agentic.ai.api.chat.ChatClientCapabilities;
import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.api.chat.ChatRequest;
import io.github.agentic.ai.api.chat.ChatResponse;
import io.github.agentic.ai.api.chat.FinishReason;
import io.github.agentic.ai.api.chat.Usage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InterceptorRuntimeTest {
    private static ChatClient client() {
        var client = mock(ChatClient.class);
        when(client.capabilities()).thenReturn(ChatClientCapabilities.basic());
        when(client.callBlocking(any())).thenCallRealMethod();
        when(client.call(any())).thenAnswer(invocation -> {
            ChatRequest request = invocation.getArgument(0);
            boolean selection = request.messages().stream().anyMatch(m -> m.text().contains("'tools' array"));
            String text = selection ? "{\"tools\":[\"quote_shipping\"]}" : "Use the saved checkpoint";
            return CompletableFuture.completedFuture(new ChatResponse("1", "fixture",
                List.of(ChatMessage.assistant(text)), List.of(), FinishReason.STOP, Usage.unknown(), Map.of()));
        });
        return client;
    }

    @Test
    void timingAndCachePreserveToolIdentityAndDoNotCacheErrors() throws Exception {
        var cache = new InterceptorsExample.TrackingCache();
        var calls = new java.util.concurrent.atomic.AtomicInteger();
        io.github.agentic.ai.graph.agent.interceptor.ToolCallHandler handler = request -> {
            calls.incrementAndGet();
            return io.github.agentic.ai.graph.agent.interceptor.ToolCallResponse.success(request.getToolCallId(), request.getToolName(), "Friday");
        };
        for (String id : List.of("a", "b")) {
            var request = io.github.agentic.ai.graph.agent.interceptor.ToolCallRequest.builder()
                .toolCall(new io.github.agentic.ai.api.chat.ToolCall(id, "track_order", "{}"))
                .context(Map.of()).build();
            assertEquals(id, cache.interceptToolCall(request, handler).getToolCallId());
        }
        assertEquals(1, calls.get());
        var other = io.github.agentic.ai.graph.agent.interceptor.ToolCallRequest.builder()
            .toolCall(new io.github.agentic.ai.api.chat.ToolCall("other", "other", "{}"))
            .context(Map.of()).build();
        cache.interceptToolCall(other, handler);
        assertEquals(2, calls.get());
        var client = client();
        try (var factory = mockStatic(SpringAiExample.class)) {
            factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            InterceptorsExample.main(new String[0]);
            ToolPolicyScenarios.main(new String[]{"select"});
            ToolPolicyScenarios.main(new String[]{"emulate"});
            assertThrows(IllegalArgumentException.class, () -> ToolPolicyScenarios.agent(client, "unknown"));
        }
    }

    @Test
    void shippingTariffsHaveDistinctSuccessAndFailureOutcomes() {
        var tool = new ShippingQuoteTool();
        assertEquals("quote_shipping", tool.definition().name());
        for (String argument : List.of("{}", "{\"country\":12}", "{\"country\":\"FR\"}")) {
            var call = new io.github.agentic.ai.api.chat.ToolCall("id", "quote_shipping", argument);
            assertTrue(tool.execute(call, io.github.agentic.ai.api.tool.ToolContext.empty()).toCompletableFuture().join().error());
        }
        var call = new io.github.agentic.ai.api.chat.ToolCall("id", "quote_shipping", "{\"country\":\"GB\"}");
        assertTrue(tool.execute(call, io.github.agentic.ai.api.tool.ToolContext.empty()).toCompletableFuture().join()
            .toMessage(call).text().contains("GBP 4"));
    }

    @Test
    void fallbackAgentRecoversFromARealPrimaryCallFailure() throws Exception {
        var primary = client();
        org.mockito.Mockito.doReturn(CompletableFuture.failedFuture(new IllegalStateException("Injected outage")))
            .when(primary).call(any());
        var fallback = client();
        assertEquals("Use the saved checkpoint", FallbackAgent.agent(primary, fallback).call("Resume").text());
        try (var config = mockStatic(FallbackAgent.class, org.mockito.Mockito.CALLS_REAL_METHODS);
                var factory = mockStatic(SpringAiExample.class)) {
            config.when(() -> FallbackAgent.fallbackEnvironment(System.getenv())).thenReturn(Map.of("ARGI_MODEL", "fixture"));
            factory.when(() -> SpringAiExample.client(any())).thenReturn(fallback);
            FallbackAgent.main(new String[0]);
        }
    }
}
