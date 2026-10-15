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
import io.github.agentic.ai.graph.CompiledGraph;
import io.github.agentic.ai.graph.OverAllState;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.action.AsyncNodeActionWithConfig;
import io.github.agentic.ai.graph.action.InterruptableAction;
import io.github.agentic.ai.graph.action.InterruptionMetadata;
import io.github.agentic.ai.graph.checkpoint.config.SaverConfig;
import io.github.agentic.ai.graph.checkpoint.savers.MemorySaver;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;

/** A dynamic interrupt is raised only when the state lacks a review decision. */
public final class DynamicApproval {
    private DynamicApproval() {}
    public static final class Approval implements AsyncNodeActionWithConfig, InterruptableAction {
        @Override public Optional<InterruptionMetadata> interrupt(String id, OverAllState state, RunnableConfig config) {
            return state.value("approved").isPresent() ? Optional.empty()
                : Optional.of(InterruptionMetadata.builder(id, state).build());
        }
        @Override public CompletableFuture<Map<String, Object>> apply(OverAllState state, RunnableConfig config) {
            return CompletableFuture.completedFuture(Map.of("status", state.<Boolean>value("approved").orElseThrow() ? "approved" : "rejected"));
        }
    }
    public static CompiledGraph workflow() throws Exception {
        return new StateGraph().addNode("review", new Approval()).addEdge(START, "review").addEdge("review", END)
            .compile(CompileConfig.builder().saverConfig(SaverConfig.builder().register(new MemorySaver()).build()).build());
    }
    public static void main(String[] args) throws Exception {
        var graph = workflow();
        var config = RunnableConfig.builder().threadId("review-42").build();
        graph.stream(Map.of("change", "Publish release notes"), config).collectList().block();
        var resume = graph.updateState(config, Map.of("approved", true)).withResume();
        org.slf4j.LoggerFactory.getLogger(DynamicApproval.class).info("{}", graph.invoke((Map<String, Object>) null, resume).orElseThrow().data());
    }
}
