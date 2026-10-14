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
import java.util.Map;
import static io.github.agentic.ai.graph.StateGraph.*;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;
import static io.github.agentic.ai.graph.action.AsyncEdgeAction.edge_async;

/** Runs independent order checks in parallel, then delegates to a nested graph. */
public final class ParallelSubgraph {
    private ParallelSubgraph() {}

    public static CompiledGraph workflow() throws Exception {
        var receipt = new StateGraph()
            .addNode("format", node_async(state -> Map.of("receipt", state.value("stock").orElseThrow() + "/" + state.value("payment").orElseThrow())))
            .addEdge(START, "format").addEdge("format", END);
        return new StateGraph()
            .addNode("stock", node_async(state -> Map.of("stock", "reserved")))
            .addNode("payment", node_async(state -> Map.of("payment", "authorized")))
            .addNode("receipt", receipt)
            .addEdge(START, "stock").addEdge(START, "payment")
            .addEdge("stock", "receipt").addEdge("payment", "receipt").addEdge("receipt", END).compile();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(ParallelSubgraph.class).info("{}", workflow().invoke(Map.of()).orElseThrow().data());
    }
}
