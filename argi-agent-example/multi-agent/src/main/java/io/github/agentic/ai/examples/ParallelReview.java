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

import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.flow.agent.ParallelAgent;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import org.slf4j.LoggerFactory;

/** Combines independent reviewers with a concurrency bound and an application merge policy. */
public final class ParallelReview {
    private ParallelReview() {}

    public static ParallelAgent workflow(ChatClient client) {
        return workflow(client, ForkJoinPool.commonPool());
    }

    public static ParallelAgent workflow(ChatClient client, Executor executor) {
        var correctness = ReactAgent.builder().throwOnModelError(true).name("correctness").chatClient(client)
            .instruction("Review checkpoint correctness and recovery invariants.")
            .outputKey("correctness_review").build();
        var operability = ReactAgent.builder().throwOnModelError(true).name("operability").chatClient(client)
            .instruction("Review shutdown, recovery diagnostics and operator decisions.")
            .outputKey("operations_review").build();
        return ParallelAgent.builder().executor(executor).name("parallel_review")
            .subAgents(List.of(correctness, operability)).maxConcurrency(2)
            .mergeStrategy(new ReviewMergeStrategy()).mergeOutputKey("review_report").build();
    }

    public static void main(String[] args) throws Exception {
        var workers = Executors.newFixedThreadPool(2);
        try {
            var config = RunnableConfig.builder().defaultParallelExecutor(workers).build();
            var result = workflow(SpringAiExample.client(System.getenv()), workers)
                .invoke("Review a support workflow with durable checkpoints and human approval.", config).orElseThrow();
            org.slf4j.LoggerFactory.getLogger(ParallelReview.class).info("{}", result.value("review_report").orElseThrow());
        } finally {
            workers.shutdownNow();
        }
    }
}
