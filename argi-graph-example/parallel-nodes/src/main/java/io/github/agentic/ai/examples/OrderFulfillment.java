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
import io.github.agentic.ai.graph.KeyStrategyFactory;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.state.strategy.AppendStrategy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import org.slf4j.LoggerFactory;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncEdgeAction.edge_async;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Joins all checks before choosing whether an order is eligible for fulfillment. */
public final class OrderFulfillment {
    private OrderFulfillment() {}

    public static CompiledGraph workflow() throws Exception {
        KeyStrategyFactory keys = () -> Map.of("checks", new AppendStrategy());
        return new StateGraph(keys)
            .addNode("stock", node_async(OrderChecks::stock))
            .addNode("credit", node_async(OrderChecks::credit))
            .addNode("shipping", node_async(OrderChecks::shipping))
            .addNode("join", node_async(OrderChecks::join))
            .addNode("ready", node_async(state -> Map.of("status", "ready-to-fulfill")))
            .addNode("hold", node_async(state -> Map.of("status", "held-for-review")))
            .addEdge(START, "stock").addEdge(START, "credit").addEdge(START, "shipping")
            .addEdge("stock", "join").addEdge("credit", "join").addEdge("shipping", "join")
            .addConditionalEdges("join", edge_async(state ->
                state.<Boolean>value("ready").orElseThrow() ? "ready" : "hold"),
                Map.of("ready", "ready", "hold", "hold"))
            .addEdge("ready", END).addEdge("hold", END).compile();
    }

    public static Map<String, Object> evaluate(boolean stock, boolean credit, boolean shipping) throws Exception {
        var workers = Executors.newFixedThreadPool(3);
        try {
            return workflow().invoke(Map.of("in_stock", stock, "credit_approved", credit,
                "shipping_supported", shipping), RunnableConfig.builder().defaultParallelExecutor(workers).build())
                .orElseThrow().data();
        } finally {
            workers.shutdownNow();
        }
    }

    public static void main(String[] args) throws Exception {
        for (boolean credit : List.of(true, false)) {
            LoggerFactory.getLogger(OrderFulfillment.class).info("creditApproved={} result={}",
                credit, evaluate(true, credit, true));
        }
    }
}
