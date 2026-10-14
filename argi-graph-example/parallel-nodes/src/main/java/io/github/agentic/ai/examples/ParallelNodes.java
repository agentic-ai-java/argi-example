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

import io.github.agentic.ai.graph.state.strategy.AppendStrategy;
import java.util.concurrent.Executors;
import java.time.Duration;

/** Aggregates independent checks using an explicitly owned worker pool. */
public final class ParallelNodes {
    private ParallelNodes() {}
    public static List<NodeOutput> run() throws Exception {
        KeyStrategyFactory keys = () -> Map.of("checks", new AppendStrategy());
        var graph = new StateGraph(keys)
            .addNode("stock", node_async(s -> Map.of("checks", List.of("stock: available"))))
            .addNode("credit", node_async(s -> Map.of("checks", List.of("credit: approved"))))
            .addNode("shipping", node_async(s -> Map.of("checks", List.of("shipping: supported"))))
            .addNode("join", node_async(s -> Map.of("ready", s.<List<?>>value("checks").orElseThrow().size() == 3)))
            .addEdge(START, "stock").addEdge(START, "credit").addEdge(START, "shipping")
            .addEdge("stock", "join").addEdge("credit", "join").addEdge("shipping", "join").addEdge("join", END).compile();
        var workers = Executors.newFixedThreadPool(3);
        try {
            return graph.stream(Map.of(), RunnableConfig.builder().defaultParallelExecutor(workers).build())
                .collectList().block(Duration.ofSeconds(10));
        } finally { workers.shutdownNow(); }
    }
    public static void main(String[] args) throws Exception {
        run().forEach(step -> org.slf4j.LoggerFactory.getLogger(ParallelNodes.class).info("{}: {}", step.node(), step.state().data()));
    }
}
