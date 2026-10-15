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
import org.slf4j.LoggerFactory;

/** Exposes a typed specialist input while keeping parent reasoning out of the child context. */
public final class StructuredDelegation {
    private StructuredDelegation() {}

    public static ReactAgent coordinator(ChatClient client) {
        var reviewer = ReactAgent.builder().throwOnModelError(true).name("structured_java_review").chatClient(client)
            .description("Review source code with explicit language and code fields")
            .inputType(ReviewRequest.class).includeContents(false).returnReasoningContents(false)
            .instruction("Review the supplied code for resource lifecycle problems. Explain concrete fixes.")
            .build();
        return ReactAgent.builder().throwOnModelError(true).name("structured_coordinator").chatClient(client)
            .toolRegistry(new ToolRegistry(List.of(AgentTool.create(reviewer))))
            .instruction("Delegate reviews using structured_java_review and populate its input schema.").build();
    }

    public static void main(String[] args) throws Exception {
        var result = coordinator(SpringAiExample.client(System.getenv()))
            .call("Review Java code: Executors.newFixedThreadPool(2).submit(() -> 42);");
        LoggerFactory.getLogger(StructuredDelegation.class).info("{}", result.text());
    }
}
