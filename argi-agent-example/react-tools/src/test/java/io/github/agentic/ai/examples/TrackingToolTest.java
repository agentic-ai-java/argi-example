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
import io.github.agentic.ai.api.tool.ToolRegistry;
import java.util.List;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TrackingToolTest {
    @Test
    void preservesCallIdentityAndSeparatesMissingDataFromDispatchFailure() {
        var registry = new ToolRegistry(List.of(new TrackingTool(OrderRepository.sample())));
        var found = new ToolCall("call-42", "track_order", "{\"orderId\":\"ORDER-42\"}");
        var result = registry.execute(found, ToolContext.empty()).toCompletableFuture().join();
        assertFalse(result.error());
        assertEquals("call-42", result.toMessage(found).toolCallId());
        assertEquals("track_order", result.toMessage(found).toolName());
        assertTrue(result.toMessage(found).text().contains("Friday"));
        var missing = new ToolCall("call-404", "track_order", "{\"orderId\":\"missing\"}");
        assertTrue(registry.execute(missing, ToolContext.empty()).toCompletableFuture().join().error());
        var failure = assertThrows(CompletionException.class, () -> registry.execute(
            new ToolCall("unknown", "unknown", "{}"), ToolContext.empty()).toCompletableFuture().join());
        assertTrue(failure.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void rejectsMalformedAndSchemaInvalidArguments() {
        var tool = new TrackingTool(OrderRepository.sample());
        for (String arguments : List.of("{", "{}", "null", "[]", "{\"orderId\":42}",
                "{\"orderId\":\"\"}", "{\"orderId\":\"ORDER-42\",\"extra\":true}")) {
            assertTrue(tool.execute(new ToolCall("invalid", "track_order", arguments), ToolContext.empty())
                .toCompletableFuture().join().error(), arguments);
        }
    }

    @Test
    void directToolKeepsTheSameBusinessResult() {
        var tool = new DirectTrackingTool();
        assertTrue(tool.returnDirect());
        var call = new ToolCall("direct", tool.definition().name(), "{\"orderId\":\"ORDER-43\"}");
        assertEquals("Preparing shipment", tool.execute(call, ToolContext.empty())
            .toCompletableFuture().join().toMessage(call).text());
    }
}
