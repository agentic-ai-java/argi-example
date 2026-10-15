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

import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.graph.agent.interceptor.ModelRequest;
import io.github.agentic.ai.graph.agent.interceptor.ModelResponse;
import io.github.agentic.ai.graph.agent.interceptor.contextediting.ContextEditingInterceptor;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

/** Shows outbound tool-result compaction independently of provider response variability. */
public final class ContextEditingScenarios {
    private ContextEditingScenarios() {}

    public static void main(String[] args) {
        var history = new ArrayList<ChatMessage>();
        history.add(ChatMessage.user("Compare the tracking history of these orders."));
        for (int index = 1; index <= 4; index++) {
            String id = "tracking-" + index;
            history.add(ChatMessage.assistant("", List.of(new ToolCall(id, "track_order", "{\"orderId\":\"ORDER-42\"}"))));
            history.add(ChatMessage.tool(id, "track_order", "Historical tracking detail. ".repeat(200)));
        }
        var request = ModelRequest.builder().messages(history).build();
        var editor = ContextEditingInterceptor.builder().trigger(200).clearAtLeast(100)
            .keep(1).clearToolInputs(false).placeholder("[older tracking details removed]").build();
        editor.interceptModel(request, edited -> {
            for (var message : edited.getMessages()) {
                LoggerFactory.getLogger(ContextEditingScenarios.class).info(
                    "role={} toolCallId={} textLength={}", message.role(), message.toolCallId(), message.text().length());
            }
            // The terminal handler is deliberately local: this scenario inspects request transformation.
            return ModelResponse.of(ChatMessage.assistant("Outbound request inspected locally."));
        });
        LoggerFactory.getLogger(ContextEditingScenarios.class).info("Original history retained: {} messages", history.size());
    }
}
