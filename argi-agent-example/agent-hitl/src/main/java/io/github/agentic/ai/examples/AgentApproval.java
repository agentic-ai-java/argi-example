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

import io.github.agentic.ai.api.chat.*;
import io.github.agentic.ai.api.tool.*;
import io.github.agentic.ai.graph.agent.ReactAgent;
import java.util.*;

import io.github.agentic.ai.graph.*;
import io.github.agentic.ai.graph.action.InterruptionMetadata;
import io.github.agentic.ai.graph.action.InterruptionMetadata.ToolFeedback;
import io.github.agentic.ai.graph.action.InterruptionMetadata.ToolFeedback.FeedbackResult;
import io.github.agentic.ai.graph.agent.hook.hip.HumanInTheLoopHook;
import io.github.agentic.ai.graph.checkpoint.savers.MemorySaver;
import java.time.Duration;

/** Uses a read-only tool to demonstrate approval mechanics without an external side effect. */
public final class AgentApproval {
    private AgentApproval() {}
    public static ReactAgent agent(ChatClient client) {
        return ReactAgent.builder().name("approved_tracking").chatClient(client).saver(new MemorySaver())
            .toolRegistry(new ToolRegistry(List.of(ReactTools.trackingTool())))
            .hooks(List.of(HumanInTheLoopHook.builder().approvalOn("track_order", "Approve access to the order record").build())).build();
    }
    public static RunnableConfig decision(RunnableConfig config, InterruptionMetadata interruption, FeedbackResult decision) {
        var feedback = interruption.toolFeedbacks().stream().map(tool -> ToolFeedback.builder(tool)
            .result(decision).arguments(decision == FeedbackResult.EDITED ? "{\"orderId\":\"ORDER-43\"}" : tool.getArguments()).build()).toList();
        return RunnableConfig.builder(config).addHumanFeedback(InterruptionMetadata.builder(interruption).toolFeedbacks(feedback).build()).resume().build();
    }
    public static void main(String[] args) throws Exception {
        FeedbackResult choice = FeedbackResult.valueOf(args.length == 0 ? "REJECTED" : args[0]);
        var agent = agent(SpringAiExample.client(System.getenv()));
        var config = RunnableConfig.builder().threadId("approval-42").build();
        var output = agent.invokeAndGetOutput("Use track_order to look up ORDER-42.", config).orElseThrow();
        if (!(output instanceof InterruptionMetadata interruption)) throw new IllegalStateException("Model did not request approval");
        agent.stream((Map<String, Object>) null, decision(config, interruption, choice))
            .doOnNext(step -> org.slf4j.LoggerFactory.getLogger(AgentApproval.class).info("{}", step)).blockLast(Duration.ofSeconds(90));
    }
}
