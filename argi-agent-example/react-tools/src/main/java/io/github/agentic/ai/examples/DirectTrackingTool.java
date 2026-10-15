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
import io.github.agentic.ai.api.tool.ToolRegistry;
import io.github.agentic.ai.api.tool.ToolResult;
import io.github.agentic.ai.graph.agent.ReactAgent;
import java.util.List;
import java.util.concurrent.CompletionStage;
import org.slf4j.LoggerFactory;

/** Returns the application result directly instead of asking the model to rewrite it. */
public final class DirectTrackingTool implements Tool {
    private final TrackingTool delegate = new TrackingTool(OrderRepository.sample());

    @Override
    public ToolDefinition definition() {
        return delegate.definition();
    }

    @Override
    public boolean returnDirect() {
        return true;
    }

    @Override
    public CompletionStage<ToolResult> execute(ToolCall call, ToolContext context) {
        return delegate.execute(call, context);
    }

    public static void main(String[] args) throws Exception {
        var agent = ReactAgent.builder().throwOnModelError(true).name("direct_tracking")
            .chatClient(SpringAiExample.client(System.getenv()))
            .instruction("Use track_order to look up the requested order.")
            .toolRegistry(new ToolRegistry(List.of(new DirectTrackingTool()))).build();
        LoggerFactory.getLogger(DirectTrackingTool.class).info("{}", agent.call("Track ORDER-42.").text());
    }
}
