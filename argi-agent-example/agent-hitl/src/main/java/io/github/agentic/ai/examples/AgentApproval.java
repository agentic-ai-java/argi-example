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
import io.github.agentic.ai.api.tool.ToolRegistry;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.action.InterruptionMetadata.ToolFeedback.FeedbackResult;
import io.github.agentic.ai.graph.action.InterruptionMetadata.ToolFeedback;
import io.github.agentic.ai.graph.action.InterruptionMetadata;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.hook.hip.HumanInTheLoopHook;
import io.github.agentic.ai.graph.checkpoint.savers.MemorySaver;
import io.github.agentic.ai.graph.streaming.StreamingOutput;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** Uses a read-only tool to demonstrate approval mechanics without an external side effect. */
public final class AgentApproval {
    private AgentApproval() {}
    public static ReactAgent agent(ChatClient client) {
        return ReactAgent.builder().throwOnModelError(true).name("approved_tracking").chatClient(client).saver(new MemorySaver())
            .instruction("Look up the requested order once. A human reviewer may correct the order ID. "
                + "Accept that correction and report the returned status without retrying the original ID. "
                + "If access is rejected, explain the rejection and stop without requesting the tool again.")
            .toolRegistry(new ToolRegistry(List.of(ReactTools.trackingTool())))
            .hooks(List.of(HumanInTheLoopHook.builder().approvalOn("track_order", "Approve access to the order record").build())).build();
    }
    public static RunnableConfig decision(RunnableConfig config, InterruptionMetadata interruption, FeedbackResult decision) {
        var feedback = interruption.toolFeedbacks().stream().map(tool -> ToolFeedback.builder(tool)
            .result(decision).arguments(decision == FeedbackResult.EDITED ? "{\"orderId\":\"ORDER-43\"}" : tool.getArguments()).build()).toList();
        // Feedback already marks this as a resume. Calling resume() afterward would
        // overwrite the typed decision with the generic resume placeholder.
        return RunnableConfig.builder(config).addHumanFeedback(InterruptionMetadata.builder(interruption).toolFeedbacks(feedback).build()).build();
    }
    public static void main(String[] args) throws Exception {
        FeedbackResult choice = FeedbackResult.valueOf(args.length == 0 ? "REJECTED" : args[0]);
        var agent = agent(SpringAiExample.client(System.getenv()));
        var config = RunnableConfig.builder().threadId("approval-42").build();
        var output = agent.invokeAndGetOutput("Use track_order to look up ORDER-42.", config).orElseThrow();
        if (!(output instanceof InterruptionMetadata interruption)) throw new IllegalStateException("Model did not request approval");
        // Agent input normalization requires a non-null map; resume adds no new user turn.
        var last = agent.stream(Map.of(), decision(config, interruption, choice))
            .doOnNext(step -> {
                if (step instanceof StreamingOutput<?> streaming) {
                    streaming.messages().stream().filter(message -> !message.text().isBlank()).forEach(message ->
                        org.slf4j.LoggerFactory.getLogger(AgentApproval.class).info("{}: {}", message.role(), message.text()));
                }
            }).blockLast(Duration.ofSeconds(90));
        if (last instanceof InterruptionMetadata) {
            throw new IllegalStateException("The model requested another approval; review it before resuming");
        }
    }
}
