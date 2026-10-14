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

import io.github.agentic.ai.api.chat.*;
import io.github.agentic.ai.api.tool.*;
import io.github.agentic.ai.graph.agent.ReactAgent;
import java.util.*;

import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.agent.hook.messages.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import tools.jackson.databind.json.JsonMapper;

/** A local documentation corpus makes the retrieval strategy explicit and reproducible. */
public final class AgenticRag {
    private AgenticRag() {}
    private static final Map<String, String> PAGES = Map.of(
        "checkpoint", "A checkpoint records thread state and the next graph node.",
        "approval", "Resume approvals with the same thread and explicit human feedback.",
        "tool", "ARGI executes tools; model adapters expose only their definitions.");
    public static String retrieve(String query) {
        return PAGES.entrySet().stream().filter(e -> query.toLowerCase(Locale.ROOT).contains(e.getKey()))
            .map(e -> "[" + e.getKey() + "] " + e.getValue()).sorted().reduce((a, b) -> a + "\n" + b).orElse("No matching source.");
    }
    public static Tool searchTool() {
        return new Tool() {
            @Override public ToolDefinition definition() {
                return new ToolDefinition("search_docs", "Search the local ARGI documentation corpus",
                    "{\"type\":\"object\",\"properties\":{\"query\":{\"type\":\"string\"}},\"required\":[\"query\"]}");
            }
            @Override public CompletionStage<ToolResult> execute(ToolCall call, ToolContext context) {
                return CompletableFuture.completedFuture(ToolResult.text(retrieve(JsonMapper.shared().readTree(call.arguments()).path("query").asString())));
            }
        };
    }
    public static MessagesModelHook evidenceHook() {
        return new MessagesModelHook() {
            @Override public String getName() { return "source_evidence"; }
            @Override public AgentCommand beforeModel(List<ChatMessage> messages, RunnableConfig config) {
                String question = messages.stream().filter(m -> m.role() == ChatRole.USER).reduce((a, b) -> b).orElseThrow().text();
                var augmented = new ArrayList<>(messages);
                augmented.add(ChatMessage.system("Reference material; cite its source labels:\n" + retrieve(question)));
                return new AgentCommand(augmented);
            }
        };
    }
    public static ReactAgent agent(ChatClient client, boolean preloadEvidence) {
        var builder = ReactAgent.builder().name("documentation_assistant").chatClient(client)
            .instruction("Use search_docs when more evidence is needed. Cite source labels. Say when a source is missing.")
            .toolRegistry(new ToolRegistry(List.of(searchTool())));
        if (preloadEvidence) builder.hooks(List.of(evidenceHook()));
        return builder.build();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(AgenticRag.class).info("{}", agent(SpringAiExample.client(System.getenv()), true)
            .call("How do checkpoint and approval work together?").text());
    }
}
