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
import io.github.agentic.ai.api.chat.ChatEvent;
import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.api.chat.ChatRequest;
import io.github.agentic.ai.api.chat.ChatResponse;
import io.github.agentic.ai.api.chat.GenerationOptions;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.LoggerFactory;

/** Exercises neutral async calls and demand-controlled streaming through this adapter. */
public final class AgentScopeScenarios {
    private AgentScopeScenarios() {}

    public static ChatRequest request() {
        return new ChatRequest(List.of(
            ChatMessage.system("Explain Java workflow concepts briefly."),
            ChatMessage.user("What does a checkpoint preserve?")),
            List.of(), GenerationOptions.builder().maxTokens(2048).build(), Map.of());
    }

    public static ChatResponse asynchronous(ChatClient client) throws Exception {
        var pending = client.call(request()).toCompletableFuture();
        try {
            return requireAnswer(pending.get(90, TimeUnit.SECONDS));
        } finally {
            if (!pending.isDone()) {
                pending.cancel(true);
            }
        }
    }

    public static ChatResponse streaming(ChatClient client) throws Exception {
        if (!client.capabilities().streaming()) {
            throw new IllegalArgumentException("This adapter does not advertise streaming");
        }
        var completed = new CompletableFuture<ChatResponse>();
        var subscription = new AtomicReference<Flow.Subscription>();
        client.stream(request()).subscribe(new Flow.Subscriber<ChatEvent>() {
            private ChatResponse response;

            @Override
            public void onSubscribe(Flow.Subscription value) {
                if (!subscription.compareAndSet(null, value)) {
                    value.cancel();
                    return;
                }
                if (completed.isDone()) {
                    value.cancel();
                } else {
                    value.request(1);
                }
            }

            @Override
            public void onNext(ChatEvent event) {
                if (completed.isDone()) {
                    subscription.get().cancel();
                    return;
                }
                switch (event.type()) {
                    case TEXT_DELTA -> LoggerFactory.getLogger(AgentScopeScenarios.class)
                        .info("Text delta: {}", event.text());
                    case TOOL_CALL -> LoggerFactory.getLogger(AgentScopeScenarios.class)
                        .info("Tool requested: {}", event.toolCall().name());
                    case USAGE -> LoggerFactory.getLogger(AgentScopeScenarios.class)
                        .info("Usage: {}", event.usage());
                    case RESPONSE -> {
                        if (response != null) {
                            completed.completeExceptionally(new IllegalStateException("Duplicate final response"));
                            subscription.get().cancel();
                            return;
                        }
                        response = event.response();
                    }
                }
                subscription.get().request(1);
            }

            @Override
            public void onError(Throwable failure) {
                completed.completeExceptionally(failure);
            }

            @Override
            public void onComplete() {
                if (response == null) {
                    completed.completeExceptionally(new IllegalStateException("Stream ended without a final response"));
                } else {
                    completed.complete(response);
                }
            }
        });
        try {
            return requireAnswer(completed.get(90, TimeUnit.SECONDS));
        } finally {
            // Also cancel if onSubscribe arrives after the caller's deadline.
            completed.cancel(true);
            var current = subscription.get();
            if (current != null) {
                current.cancel();
            }
        }
    }

    private static ChatResponse requireAnswer(ChatResponse response) {
        if (response.text() == null || response.text().isBlank()) {
            throw new IllegalStateException("Model returned no visible answer; finish=" + response.finishReason());
        }
        if (response.finishReason() == io.github.agentic.ai.api.chat.FinishReason.LENGTH) {
            throw new IllegalStateException("Model exhausted its output budget before completing the answer");
        }
        return response;
    }

    public static void main(String[] args) throws Exception {
        var client = AgentScopeExample.client(System.getenv());
        var log = LoggerFactory.getLogger(AgentScopeScenarios.class);
        log.info("Adapter capabilities: {}", client.capabilities());
        var response = asynchronous(client);
        log.info("Async answer={} finish={} usage={}", response.text(), response.finishReason(), response.usage());
        if (client.capabilities().streaming()) {
            log.info("Streamed answer: {}", streaming(client).text());
        } else {
            log.info("Streaming was not requested because this adapter does not advertise it");
        }
    }
}
