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

import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.api.tool.ToolContext;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallRequest;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallResponse;
import io.github.agentic.ai.graph.agent.interceptor.toolretry.ToolRetryInterceptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RetryScenariosTest {
    @Test
    void retriesAnExceptionalStageAndKeepsTheOriginalInvocationIdentity() {
        var tool = new FailOnceTrackingTool();
        var call = new ToolCall("retry-42", "track_order", "{\"orderId\":\"ORDER-42\"}");
        var interceptor = ToolRetryInterceptor.builder().toolName("track_order")
            .maxAttempts(2).initialDelay(0).maxDelay(0).jitter(false).build();
        var request = ToolCallRequest.builder().toolCall(call).build();
        var response = interceptor.interceptToolCall(request, current -> {
            var result = tool.execute(call, ToolContext.empty()).toCompletableFuture().join();
            return ToolCallResponse.success(current.getToolCallId(), current.getToolName(),
                result.toMessage(call).text());
        });
        assertEquals(2, tool.attempts());
        assertEquals("retry-42", response.getToolCallId());
        assertTrue(response.getResult().contains("Friday"));
    }
}
