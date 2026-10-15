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
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** A two-step, model-free onboarding workflow. */
public final class HelloGraph {
    private HelloGraph() {}

    public static CompiledGraph workflow() throws Exception {
        return new StateGraph()
            .addNode("normalize", node_async(state -> Map.of("name", state.<String>value("name").orElseThrow().strip())))
            .addNode("welcome", node_async(state -> Map.of("greeting", "Welcome, " + state.value("name").orElseThrow() + "!")))
            .addEdge(START, "normalize").addEdge("normalize", "welcome").addEdge("welcome", END).compile();
    }
    public static void main(String[] args) throws Exception {
        var state = workflow().invoke(Map.of("name", " Ada ")).orElseThrow();
        org.slf4j.LoggerFactory.getLogger(HelloGraph.class).info("{}", state.data());
    }
}
