/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.agentic.ai.examples;

import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.api.chat.ChatClientCapabilities;
import io.github.agentic.ai.api.chat.ChatEvent;
import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.api.chat.ChatRequest;
import io.github.agentic.ai.api.chat.ChatResponse;
import io.github.agentic.ai.graph.agent.action.ActionDecision;
import io.github.agentic.ai.graph.agent.interceptor.ModelRequest;
import io.github.agentic.ai.graph.agent.interceptor.ModelResponse;
import io.github.agentic.ai.graph.agent.interceptor.toolselection.ToolSelectionFailedException;
import io.github.agentic.ai.graph.agent.interceptor.toolselection.ToolSelectionFailurePolicy;
import io.github.agentic.ai.graph.agent.interceptor.toolselection.ToolSelectionInterceptor;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A deliberate offline failure drill, not a model-quality demonstration. */
public final class SelectionFailurePolicies {
    private static final Logger LOG = LoggerFactory.getLogger(SelectionFailurePolicies.class);

    private SelectionFailurePolicies() {
    }

    public record Selection(List<String> tools, ActionDecision decision) {
    }

    public static Selection select(ChatClient selectionClient, ToolSelectionFailurePolicy policy) {
        final var selection = ToolSelectionInterceptor.builder()
                .chatClient(selectionClient).maxTools(1).alwaysInclude("help")
                .failurePolicy(policy).build();
        final AtomicReference<ModelRequest> downstream = new AtomicReference<>();
        selection.interceptModel(ModelRequest.builder()
                .messages(List.of(ChatMessage.user("Help me review an invoice")))
                .tools(List.of("help", "reviewInvoice", "chargeCard")).build(), request -> {
                    downstream.set(request);
                    return ModelResponse.of(ChatMessage.assistant("Selection inspected locally."));
                });
        return new Selection(downstream.get().getTools(),
                (ActionDecision) downstream.get().getContext().get(ActionDecision.CONTEXT_KEY));
    }

    /** Injects unavailability without opening a network connection. */
    public static ChatClient unavailableClient() {
        return new ChatClient() {
            @Override
            public CompletionStage<ChatResponse> call(ChatRequest request) {
                return CompletableFuture.failedFuture(new IllegalStateException("Injected selector outage"));
            }

            @Override
            public Flow.Publisher<ChatEvent> stream(ChatRequest request) {
                return subscriber -> subscriber.onSubscribe(new Flow.Subscription() {
                    private boolean terminated;

                    @Override
                    public void request(long count) {
                        if (!terminated) {
                            terminated = true;
                            subscriber.onError(new IllegalStateException("Injected selector outage"));
                        }
                    }

                    @Override
                    public void cancel() {
                        terminated = true;
                    }
                });
            }

            @Override
            public ChatClientCapabilities capabilities() {
                return ChatClientCapabilities.basic();
            }
        };
    }

    public static void main(String[] args) {
        for (ToolSelectionFailurePolicy policy : ToolSelectionFailurePolicy.values()) {
            try {
                LOG.info("{}: {}", policy, select(unavailableClient(), policy));
            }
            catch (ToolSelectionFailedException expected) {
                LOG.info("{}: stopped with {}", policy, expected.getDecision());
            }
        }
    }
}
