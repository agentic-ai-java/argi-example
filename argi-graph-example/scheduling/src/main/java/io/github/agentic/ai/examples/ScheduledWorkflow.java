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

import io.github.agentic.ai.graph.scheduling.*;
import java.util.concurrent.*;

/** A bounded heartbeat demonstration with explicit scheduler cleanup. */
public final class ScheduledWorkflow {
    private ScheduledWorkflow() {}
    public static void main(String[] args) throws Exception {
        var completed = new CountDownLatch(2);
        var graph = new StateGraph().addNode("heartbeat", node_async(s -> {
            completed.countDown();
            return Map.of("checked_at", java.time.Instant.now().toString());
        })).addEdge(START, "heartbeat").addEdge("heartbeat", END).compile();
        var task = new ScheduledAgentTask(graph, ScheduleConfig.builder().fixedDelay(1000).initialDelay(0).inputs(Map.of()).build());
        try {
            task.start();
            if (!completed.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Scheduled runs did not finish");
        } finally {
            task.stop();
            ScheduledAgentManagerFactory.getInstance().getManager().shutdown();
        }
    }
}
