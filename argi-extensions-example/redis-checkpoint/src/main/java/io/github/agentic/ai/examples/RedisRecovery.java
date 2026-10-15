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
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.checkpoint.config.SaverConfig;
import io.github.agentic.ai.graph.persistence.redis.RedisSaver;
import java.util.Map;
import java.util.UUID;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Demonstrates checkpoint recovery through a fresh graph using the selected external saver. */
public final class RedisRecovery {
    private RedisRecovery() {}

    public static CompiledGraph workflow(RedisSaver saver) throws Exception {
        return new StateGraph()
            .addNode("prepare", node_async(state -> Map.of("status", "pending")))
            .addNode("review", node_async(state -> Map.of("status",
                state.<Boolean>value("approved").orElseThrow() ? "approved" : "rejected")))
            .addEdge(START, "prepare").addEdge("prepare", "review").addEdge("review", END)
            .compile(CompileConfig.builder()
                .saverConfig(SaverConfig.builder().register(saver).build())
                .interruptBefore("review").build());
    }

    public static Map<String, Object> exercise(RedisSaver writer, RedisSaver reader) throws Exception {
        String prefix = "argi-recovery-" + UUID.randomUUID();
        var first = RunnableConfig.builder().threadId(prefix + "-first").build();
        var second = RunnableConfig.builder().threadId(prefix + "-second").build();
        var initial = workflow(writer);
        initial.invoke(Map.of("request", "first"), first);
        initial.invoke(Map.of("request", "second"), second);
        var restored = workflow(reader);
        var resume = restored.updateState(first, Map.of("approved", true)).withResume();
        var result = restored.invoke((Map<String, Object>) null, resume).orElseThrow();
        return Map.of("resumed", result.data(), "untouched", restored.getState(second).state().data(),
            "historySize", restored.getStateHistory(first).size(), "threadPrefix", prefix);
    }
}
