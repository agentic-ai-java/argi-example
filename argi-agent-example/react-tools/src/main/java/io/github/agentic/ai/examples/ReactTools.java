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
import io.github.agentic.ai.api.tool.Tool;
import io.github.agentic.ai.api.tool.ToolRegistry;
import io.github.agentic.ai.graph.agent.ReactAgent;
import java.util.List;

/** A delivery assistant whose tracking results come from an application tool. */
public final class ReactTools {
    private ReactTools() {}

    public static Tool trackingTool() {
        return new TrackingTool(OrderRepository.sample());
    }
    public static ReactAgent agent(ChatClient client) {
        return ReactAgent.builder().throwOnModelError(true).name("delivery_assistant").chatClient(client)
            .instruction("Use track_order for delivery questions. Do not invent delivery details.")
            .toolRegistry(new ToolRegistry(List.of(trackingTool()))).build();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(ReactTools.class).info("{}", agent(SpringAiExample.client(System.getenv())).call("Where is ORDER-42?").text());
    }
}
