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

/** Reuses one compiled child twice; both stages operate on the shared state contract. */
public final class SubgraphAsCompiledGraph {
    private SubgraphAsCompiledGraph() {}
    public static CompiledGraph workflow() throws Exception {
        var child = new StateGraph().addNode("increment", node_async(s -> Map.of("count", s.<Integer>value("count").orElse(0) + 1)))
            .addEdge(START, "increment").addEdge("increment", END).compile();
        return new StateGraph().addNode("first", child).addNode("second", child)
            .addEdge(START, "first").addEdge("first", "second").addEdge("second", END).compile();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(SubgraphAsCompiledGraph.class).info("{}", workflow().invoke(Map.of("count", 0)).orElseThrow().data());
    }
}
