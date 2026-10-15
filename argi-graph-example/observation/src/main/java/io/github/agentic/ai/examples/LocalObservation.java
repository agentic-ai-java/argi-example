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

import io.github.agentic.ai.graph.CompileConfig;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.observation.GraphObservationLifecycleListener;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** A lightweight local handler counts completed observations without exporting prompt data. */
public final class LocalObservation {
    private LocalObservation() {}
    public static int run() throws Exception {
        var stopped = new AtomicInteger();
        var registry = ObservationRegistry.create();
        registry.observationConfig().observationHandler(new ObservationHandler<Observation.Context>() {
            @Override public boolean supportsContext(Observation.Context context) { return true; }
            @Override public void onStop(Observation.Context context) {
                stopped.incrementAndGet();
                org.slf4j.LoggerFactory.getLogger(LocalObservation.class).info("Observation completed: {}", context.getName());
            }
        });
        var graph = new StateGraph().addNode("calculate", node_async(s -> Map.of("answer", 42)))
            .addEdge(START, "calculate").addEdge("calculate", END)
            .compile(CompileConfig.builder().observationRegistry(registry)
                .withLifecycleListener(new GraphObservationLifecycleListener(registry)).build());
        graph.invoke(Map.of());
        return stopped.get();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(LocalObservation.class).info("Completed observations: {}", run());
    }
}
