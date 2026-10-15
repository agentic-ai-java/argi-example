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
import io.github.agentic.ai.api.chat.ToolDefinition;
import io.github.agentic.ai.api.tool.Tool;
import io.github.agentic.ai.api.tool.ToolContext;
import io.github.agentic.ai.api.tool.ToolResult;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import tools.jackson.databind.json.JsonMapper;

/** Validates model-supplied arguments before looking up an application record. */
public final class TrackingTool implements Tool {
    private final OrderRepository repository;

    public TrackingTool(OrderRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    public ToolDefinition definition() {
        return new ToolDefinition("track_order", "Look up the delivery status of an order", """
            {"type":"object","properties":{"orderId":{"type":"string","minLength":1}},
             "required":["orderId"],"additionalProperties":false}
            """);
    }

    @Override
    public CompletionStage<ToolResult> execute(ToolCall call, ToolContext context) {
        // The schema guides the model; application validation still owns the boundary.
        String id;
        try {
            var arguments = JsonMapper.shared().readTree(call.arguments());
            if (arguments == null) {
                return CompletableFuture.completedFuture(ToolResult.error("Tool arguments must be a JSON object"));
            }
            var orderId = arguments.path("orderId");
            if (!arguments.isObject() || arguments.size() != 1 || !orderId.isString()
                    || orderId.asString().isBlank()) {
                return CompletableFuture.completedFuture(ToolResult.error("Provide exactly one non-empty orderId string"));
            }
            id = orderId.asString();
        } catch (tools.jackson.core.JacksonException invalidJson) {
            return CompletableFuture.completedFuture(ToolResult.error("Tool arguments must be valid JSON"));
        }
        // Missing data is a model-visible error, not a transport failure eligible for retry.
        ToolResult result = repository.find(id)
            .map(order -> ToolResult.text(order.status()))
            .orElseGet(() -> ToolResult.error("Unknown order: " + id));
        return CompletableFuture.completedFuture(result);
    }
}
