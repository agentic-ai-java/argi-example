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
import io.github.agentic.ai.api.chat.ChatEvent;
import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.api.chat.ChatResponse;
import io.github.agentic.ai.api.chat.FinishReason;
import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.api.chat.Usage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class LangChain4jScenariosTest {
    @Test
    void consumesDeltasAndReturnsOnlyTheAggregateResponse() throws Exception {
        var client = mock(ChatClient.class);
        when(client.capabilities()).thenReturn(new ChatClientCapabilities(true, false, false, false, true, true));
        var response = new ChatResponse("id", "fixture", List.of(ChatMessage.assistant("Saved state")),
            List.of(), FinishReason.STOP, Usage.unknown(), Map.of());
        when(client.call(any())).thenReturn(CompletableFuture.completedFuture(response));
        var subscription = mock(Flow.Subscription.class);
        when(client.stream(any())).thenReturn(subscriber -> {
            subscriber.onSubscribe(subscription);
            subscriber.onNext(ChatEvent.textDelta("Saved state"));
            subscriber.onNext(ChatEvent.response(response));
            subscriber.onComplete();
        });
        assertSame(response, LangChain4jScenarios.asynchronous(client));
        assertSame(response, LangChain4jScenarios.streaming(client));
        verify(subscription, times(3)).request(1);
        verify(subscription).cancel();
    }

    @Test
    void doesNotTreatAnIncompleteOrFailedStreamAsSuccess() {
        var client = mock(ChatClient.class);
        when(client.capabilities()).thenReturn(new ChatClientCapabilities(true, false, false, false, true, true));
        var subscription = mock(Flow.Subscription.class);
        when(client.stream(any())).thenReturn(subscriber -> {
            subscriber.onSubscribe(subscription);
            subscriber.onComplete();
        });
        var missing = assertThrows(ExecutionException.class, () -> LangChain4jScenarios.streaming(client));
        assertTrue(missing.getCause() instanceof IllegalStateException);
        var failure = new IllegalStateException("Injected stream failure");
        when(client.stream(any())).thenReturn(subscriber -> {
            subscriber.onSubscribe(mock(Flow.Subscription.class));
            subscriber.onError(failure);
        });
        assertSame(failure, assertThrows(ExecutionException.class,
            () -> LangChain4jScenarios.streaming(client)).getCause());
        when(client.capabilities()).thenReturn(ChatClientCapabilities.basic());
        assertThrows(IllegalArgumentException.class, () -> LangChain4jScenarios.streaming(client));
    }

    @Test
    void mainUsesAdvertisedCapabilitiesAndConsumesUsageAndToolEvents() throws Exception {
        var client = mock(ChatClient.class);
        var response = new ChatResponse("id", "fixture", List.of(ChatMessage.assistant("Saved state")),
            List.of(), FinishReason.STOP, Usage.unknown(), Map.of());
        when(client.call(any())).thenReturn(CompletableFuture.completedFuture(response));
        when(client.capabilities()).thenReturn(new ChatClientCapabilities(true, true, false, false, true, true));
        when(client.stream(any())).thenReturn(subscriber -> {
            subscriber.onSubscribe(mock(Flow.Subscription.class));
            subscriber.onNext(ChatEvent.usage(Usage.unknown()));
            subscriber.onNext(ChatEvent.toolCall(new ToolCall("t1", "lookup", "{}")));
            subscriber.onNext(ChatEvent.response(response));
            subscriber.onComplete();
        });
        try (var factory = mockStatic(LangChain4jExample.class)) {
            factory.when(() -> LangChain4jExample.client(System.getenv())).thenReturn(client);
            LangChain4jScenarios.main(new String[0]);
            when(client.capabilities()).thenReturn(ChatClientCapabilities.basic());
            LangChain4jScenarios.main(new String[0]);
        }
        verify(client, times(2)).call(any());
        verify(client).stream(any());
    }

    @Test
    void rejectsEmptyAndTruncatedAnswersAndCancelsDuplicateSubscriptions() throws Exception {
        var client = mock(ChatClient.class);
        when(client.capabilities()).thenReturn(new ChatClientCapabilities(true, false, false, false, true, true));
        for (var response : List.of(
                new ChatResponse("id", "fixture", List.of(ChatMessage.assistant("")), List.of(), FinishReason.STOP, Usage.unknown(), Map.of()),
                new ChatResponse("id", "fixture", List.of(ChatMessage.assistant("Partial")), List.of(), FinishReason.LENGTH, Usage.unknown(), Map.of()))) {
            when(client.call(any())).thenReturn(CompletableFuture.completedFuture(response));
            assertThrows(IllegalStateException.class, () -> LangChain4jScenarios.asynchronous(client));
        }
        var response = new ChatResponse("id", "fixture", List.of(ChatMessage.assistant("Saved state")),
            List.of(), FinishReason.STOP, Usage.unknown(), Map.of());
        var original = mock(Flow.Subscription.class);
        var duplicate = mock(Flow.Subscription.class);
        when(client.stream(any())).thenReturn(subscriber -> {
            subscriber.onSubscribe(original);
            subscriber.onSubscribe(duplicate);
            subscriber.onNext(ChatEvent.response(response));
            subscriber.onNext(ChatEvent.response(response));
            subscriber.onNext(ChatEvent.textDelta("late event"));
            subscriber.onComplete();
        });
        var failure = assertThrows(ExecutionException.class, () -> LangChain4jScenarios.streaming(client));
        assertTrue(failure.getCause().getMessage().contains("Duplicate final response"));
        verify(duplicate).cancel();
        verify(original, times(3)).cancel();
    }
}
