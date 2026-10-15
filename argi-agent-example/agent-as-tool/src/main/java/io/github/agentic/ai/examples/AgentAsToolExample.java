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
import io.github.agentic.ai.api.tool.ToolRegistry;
import io.github.agentic.ai.graph.agent.AgentTool;
import io.github.agentic.ai.graph.agent.ReactAgent;
import java.util.List;

/** The parent ReAct runtime owns delegation; the provider sees only tool definitions. */
public final class AgentAsToolExample {
    private AgentAsToolExample() {}
    public static ReactAgent coordinator(ChatClient client) {
        var specialist = ReactAgent.builder().throwOnModelError(true).name("java_reviewer").description("Review Java code for resource lifecycle issues")
            .chatClient(client).instruction("Identify leaked executors, streams, or connections.").build();
        return ReactAgent.builder().throwOnModelError(true).name("coordinator").chatClient(client)
            .instruction("Delegate Java lifecycle reviews to java_reviewer, then summarize the findings.")
            .toolRegistry(new ToolRegistry(List.of(AgentTool.create(specialist)))).build();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(AgentAsToolExample.class).info("{}", coordinator(SpringAiExample.client(System.getenv()))
            .call("Review: Executors.newFixedThreadPool(2).submit(() -> 42);").text());
    }
}
