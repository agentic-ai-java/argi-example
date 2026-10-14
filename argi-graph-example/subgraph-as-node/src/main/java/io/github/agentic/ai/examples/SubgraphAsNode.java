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

import io.github.agentic.ai.graph.*;
import java.util.*;
import static io.github.agentic.ai.graph.StateGraph.*;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** A node wrapper exposes only the child result and keeps its intermediate keys private. */
public final class SubgraphAsNode {
    private SubgraphAsNode() {}
    public static CompiledGraph workflow() throws Exception {
        var child = new StateGraph()
            .addNode("tokenize", node_async(s -> Map.of("tokens", s.<String>value("text").orElseThrow().strip().split("\\s+").length)))
            .addEdge(START, "tokenize").addEdge("tokenize", END).compile();
        return new StateGraph().addNode("count", node_async(parent -> {
            var result = child.invoke(Map.of("text", parent.value("article").orElseThrow())).orElseThrow();
            return Map.of("word_count", result.value("tokens").orElseThrow());
        })).addEdge(START, "count").addEdge("count", END).compile();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(SubgraphAsNode.class).info("{}", workflow().invoke(Map.of("article", "Graphs retain execution state")).orElseThrow().data());
    }
}
