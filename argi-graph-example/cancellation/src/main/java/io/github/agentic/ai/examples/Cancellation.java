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

import io.github.agentic.ai.graph.StateGraph;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import reactor.core.publisher.SignalType;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Uses one subscription; a second blockLast call would start a second graph run. */
public final class Cancellation {
    private Cancellation() {}
    public static boolean cancelInFlight() throws Exception {
        var started = new CountDownLatch(1);
        var cancelled = new CountDownLatch(1);
        var finished = new AtomicBoolean();
        var pending = new CompletableFuture<Map<String, Object>>();
        var graph = new StateGraph()
            .addNode("slow", (io.github.agentic.ai.graph.action.AsyncNodeAction) state -> {
                started.countDown();
                return pending;
            })
            .addNode("publish", node_async(s -> { finished.set(true); return Map.of("published", true); }))
            .addEdge(START, "slow").addEdge("slow", "publish").addEdge("publish", END).compile();
        var scheduler = reactor.core.scheduler.Schedulers.newSingle("cancellation-example");
        var subscription = graph.stream(Map.of()).subscribeOn(scheduler).doFinally(signal -> {
            if (signal == SignalType.CANCEL) cancelled.countDown();
        }).subscribe();
        try {
            if (!started.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Node did not start");
            subscription.dispose();
            if (!cancelled.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Cancellation was not observed");
            return !finished.get();
        } finally { subscription.dispose(); pending.cancel(true); scheduler.dispose(); }
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(Cancellation.class).info("Downstream publication prevented: {}", cancelInFlight());
    }
}
