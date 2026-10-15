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
import io.github.agentic.ai.api.chat.ChatRole;
import io.github.agentic.ai.api.chat.FinishReason;
import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.api.chat.Usage;
import io.github.agentic.ai.api.tool.ToolContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgentApprovalTest {
    @Test
    void approvedRejectedAndEditedFeedbackResumeWithoutAddingAUserTurn() throws Exception {
        for (var decision : io.github.agentic.ai.graph.action.InterruptionMetadata.ToolFeedback.FeedbackResult.values()) {
            if (!List.of("APPROVED", "REJECTED", "EDITED").contains(decision.name())) continue;
            var client = mock(ChatClient.class);
            when(client.callBlocking(any())).thenCallRealMethod();
            when(client.capabilities()).thenReturn(new ChatClientCapabilities(false, true, false, false, true, false));
            when(client.call(any())).thenAnswer(invocation -> {
                ChatRequest request = invocation.getArgument(0);
                var result = request.messages().stream().filter(m -> m.role() == ChatRole.TOOL).findFirst();
                var call = new ToolCall("tracking-1", "track_order", "{\"orderId\":\"ORDER-42\"}");
                var message = result.isPresent() ? ChatMessage.assistant(result.get().text()) : ChatMessage.assistant("", List.of(call));
                return CompletableFuture.completedFuture(new ChatResponse("1", "fixture", List.of(message),
                    message.toolCalls(), result.isPresent() ? FinishReason.STOP : FinishReason.TOOL_CALLS, Usage.unknown(), Map.of()));
            });
            when(client.stream(any())).thenAnswer(invocation -> {
                ChatRequest request = invocation.getArgument(0);
                ChatResponse response = client.call(request).toCompletableFuture().join();
                return (java.util.concurrent.Flow.Publisher<io.github.agentic.ai.api.chat.ChatEvent>) subscriber -> {
                    subscriber.onSubscribe(mock(java.util.concurrent.Flow.Subscription.class));
                    subscriber.onNext(io.github.agentic.ai.api.chat.ChatEvent.response(response));
                    subscriber.onComplete();
                };
            });
            var agent = AgentApproval.agent(client);
            var config = io.github.agentic.ai.graph.RunnableConfig.builder().threadId("approval-" + decision).build();
            var output = agent.invokeAndGetOutput("Track ORDER-42", config).orElseThrow();
            var interruption = org.junit.jupiter.api.Assertions.assertInstanceOf(
                io.github.agentic.ai.graph.action.InterruptionMetadata.class, output);
            var resumed = agent.invoke(Map.of(), AgentApproval.decision(config, interruption, decision)).orElseThrow();
            List<ChatMessage> messages = resumed.<List<ChatMessage>>value("messages").orElseThrow();
            org.junit.jupiter.api.Assertions.assertEquals(1, messages.stream()
                .filter(m -> m.role() == ChatRole.USER && m.text().equals("Track ORDER-42")).count());
            String toolResult = messages.stream().filter(m -> m.role() == ChatRole.TOOL).findFirst().orElseThrow().text();
            if (decision.name().equals("EDITED")) assertTrue(toolResult.contains("Preparing shipment"), toolResult);
            if (decision.name().equals("APPROVED")) assertTrue(toolResult.contains("Friday"), toolResult);
            try (var factory = mockStatic(SpringAiExample.class)) {
                factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
                AgentApproval.main(new String[]{decision.name()});
            }
            if (decision.name().equals("REJECTED")) org.junit.jupiter.api.Assertions.assertFalse(toolResult.contains("Friday"), toolResult);
        }
    }
}
