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
import io.github.agentic.ai.graph.CompiledGraph;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.node.LlmNode;
import java.time.Duration;
import java.util.Map;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;

/** The adapter handles the model protocol; LlmNode handles graph streaming and final state. */
public final class GraphModelStreaming {
    private GraphModelStreaming() {}
    public static CompiledGraph workflow(ChatClient client) throws Exception {
        var model = LlmNode.builder().chatClient(client).systemPromptTemplate("Explain Java runtime concepts concisely.")
            .userPromptTemplate("{question}").outputKey("answer").stream(true).build();
        return new StateGraph().addNode("model", model).addEdge(START, "model").addEdge("model", END).compile();
    }
    public static void main(String[] args) throws Exception {
        workflow(SpringAiExample.client(System.getenv())).stream(Map.of("question", "How does checkpoint recovery work?"))
            .doOnNext(event -> org.slf4j.LoggerFactory.getLogger(GraphModelStreaming.class).info("{}", event))
            .blockLast(Duration.ofSeconds(90));
    }
}
