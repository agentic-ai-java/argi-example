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
import io.github.agentic.ai.graph.agent.hook.pii.PIIDetectionHook;
import io.github.agentic.ai.graph.agent.hook.pii.PIIType;
import io.github.agentic.ai.graph.agent.hook.pii.RedactionStrategy;
import io.github.agentic.ai.graph.agent.hook.toolcalllimit.ToolCallLimitHook;
import io.github.agentic.ai.graph.agent.interceptor.modelretry.ModelRetryInterceptor;
import io.github.agentic.ai.graph.agent.interceptor.toolretry.ToolRetryInterceptor;
import java.util.List;

/** Retries apply to read-only lookup; write tools require effect-aware recovery instead. */
public final class HooksAndRetries {
    private HooksAndRetries() {}
    public static ReactAgent agent(ChatClient client) {
        return ReactAgent.builder().throwOnModelError(true).name("bounded_support").chatClient(client)
            .toolRegistry(new ToolRegistry(List.of(ReactTools.trackingTool())))
            .hooks(List.of(PIIDetectionHook.builder().piiType(PIIType.EMAIL).strategy(RedactionStrategy.REDACT).applyToInput(true).build(),
                ModelCallLimitHook.builder().runLimit(4).build(),
                ToolCallLimitHook.builder().toolName("track_order").runLimit(2).build()))
            .interceptors(List.of(ModelRetryInterceptor.builder().maxAttempts(2).initialDelay(2000).maxDelay(2000)
                .retryableExceptionPredicate(error -> error instanceof java.util.concurrent.CompletionException).build(),
                ToolRetryInterceptor.builder().toolName("track_order").maxAttempts(2).initialDelay(2000).maxDelay(2000).build()))
            .build();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(HooksAndRetries.class).info("{}", agent(SpringAiExample.client(System.getenv()))
            .call("My email is ada@example.org. Track ORDER-42.").text());
    }
}
