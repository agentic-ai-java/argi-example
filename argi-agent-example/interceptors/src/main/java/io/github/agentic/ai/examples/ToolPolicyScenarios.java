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
import io.github.agentic.ai.graph.agent.interceptor.toolemulator.ToolEmulatorInterceptor;
import io.github.agentic.ai.graph.agent.interceptor.toolselection.ToolSelectionInterceptor;
import java.util.List;
import org.slf4j.LoggerFactory;

/** Compares model-selected tools with explicitly requested tool emulation. */
public final class ToolPolicyScenarios {
    private ToolPolicyScenarios() {}

    public static ReactAgent agent(ChatClient client, String mode) {
        var builder = ReactAgent.builder().throwOnModelError(true).name("shipping_policy").chatClient(client)
            .toolRegistry(new ToolRegistry(List.of(ReactTools.trackingTool(), new ShippingQuoteTool())))
            .hooks(List.of(ModelCallLimitHook.builder().runLimit(4).build()))
            .instruction("Use the relevant tool and distinguish sample tariffs from live quotes.");
        switch (mode) {
            case "select" -> builder.interceptors(List.of(
                ToolSelectionInterceptor.builder().chatClient(client).maxTools(1).build()));
            case "emulate" -> builder.interceptors(List.of(
                ToolEmulatorInterceptor.builder().chatClient(client).addTool("quote_shipping").build()));
            default -> throw new IllegalArgumentException("Use select or emulate");
        }
        return builder.build();
    }

    public static void main(String[] args) throws Exception {
        String mode = args.length == 0 ? "select" : args[0];
        var log = LoggerFactory.getLogger(ToolPolicyScenarios.class);
        if (mode.equals("emulate")) {
            log.info("Emulation mode generates synthetic tool output; it is not a real shipping quote");
        }
        log.info("mode={} answer={}", mode, agent(SpringAiExample.client(System.getenv()), mode)
            .call("Use quote_shipping to find the sample tariff for GB.").text());
    }
}
