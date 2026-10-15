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
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.hook.modelcalllimit.ModelCallLimitHook;
import io.github.agentic.ai.graph.agent.interceptor.toolretry.ToolRetryInterceptor;
import java.util.List;
import org.slf4j.LoggerFactory;

/** Forces one transient tool failure so that the retry path is actually exercised. */
public final class RetryScenarios {
    private RetryScenarios() {}

    public static ReactAgent agent(ChatClient client, FailOnceTrackingTool tool) {
        return ReactAgent.builder().throwOnModelError(true).name("retry_tracking").chatClient(client)
            .instruction("Call track_order for ORDER-42 and report the result.")
            .toolRegistry(new ToolRegistry(List.of(tool)))
            .hooks(List.of(ModelCallLimitHook.builder().runLimit(4).build()))
            .interceptors(List.of(ToolRetryInterceptor.builder().toolName("track_order")
                .maxAttempts(2).initialDelay(2000).maxDelay(2000).build()))
            .build();
    }

    public static void main(String[] args) throws Exception {
        var tool = new FailOnceTrackingTool();
        var result = agent(SpringAiExample.client(System.getenv()), tool).call("Track ORDER-42.");
        if (tool.attempts() < 2) {
            throw new IllegalStateException("The model did not exercise the requested tool retry");
        }
        LoggerFactory.getLogger(RetryScenarios.class).info("Tool attempts={} answer={}", tool.attempts(), result.text());
    }
}
