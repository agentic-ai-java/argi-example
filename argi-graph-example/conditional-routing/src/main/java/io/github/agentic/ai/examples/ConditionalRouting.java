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

import io.github.agentic.ai.graph.CompiledGraph;
import io.github.agentic.ai.graph.StateGraph;
import java.util.Map;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncEdgeAction.edge_async;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Routes support tickets without asking a model to make a deterministic decision. */
public final class ConditionalRouting {
    private ConditionalRouting() {}

    public static CompiledGraph workflow() throws Exception {
        return new StateGraph()
            .addNode("classify", node_async(state -> Map.of("urgent", state.<Integer>value("severity").orElseThrow() >= 4)))
            .addNode("page", node_async(state -> Map.of("queue", "on-call")))
            .addNode("enqueue", node_async(state -> Map.of("queue", "business-hours")))
            .addEdge(START, "classify")
            .addConditionalEdges("classify", edge_async(state -> state.<Boolean>value("urgent").orElseThrow() ? "urgent" : "normal"),
                Map.of("urgent", "page", "normal", "enqueue"))
            .addEdge("page", END).addEdge("enqueue", END).compile();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(ConditionalRouting.class).info("{}", workflow().invoke(Map.of("severity", 5)).orElseThrow().data());
    }
}
