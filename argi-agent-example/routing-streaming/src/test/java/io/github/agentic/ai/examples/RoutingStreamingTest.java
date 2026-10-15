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

class RoutingStreamingTest {
    @Test
    void routesOnceAndCompletesAfterTheNestedTerminalUsageEvent() throws Exception {
        var client = mock(ChatClient.class);
        when(client.capabilities()).thenReturn(new ChatClientCapabilities(true, false, false, false, true, true));
        when(client.callBlocking(any())).thenCallRealMethod();
        var routing = new ChatResponse("route", "fixture", List.of(ChatMessage.assistant(
            "{\"agents\":[{\"agent\":\"technical\",\"query\":\"Resume a checkpoint\"}]}")),
            List.of(), FinishReason.STOP, Usage.unknown(), Map.of());
        when(client.call(any())).thenReturn(CompletableFuture.completedFuture(routing));
        var response = new ChatResponse("answer", "fixture", List.of(ChatMessage.assistant("Resume saved state.")),
            List.of(), FinishReason.STOP, new Usage(5L, 4L, 9L), Map.of());
        when(client.stream(any())).thenReturn(subscriber -> {
            subscriber.onSubscribe(mock(java.util.concurrent.Flow.Subscription.class));
            subscriber.onNext(io.github.agentic.ai.api.chat.ChatEvent.textDelta("Resume saved state."));
            subscriber.onNext(io.github.agentic.ai.api.chat.ChatEvent.response(response));
            subscriber.onComplete();
        });
        var messages = RoutingStreaming.router(client).streamMessages("Resume a checkpoint")
            .collectList().block(java.time.Duration.ofSeconds(5));
        assertTrue(messages.stream().anyMatch(m -> m.text().contains("Resume saved state.")));
        verify(client).call(any());
        verify(client).stream(any());
        try (var factory = mockStatic(SpringAiExample.class)) {
            factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            RoutingStreaming.main(new String[0]);
        }
    }
}
