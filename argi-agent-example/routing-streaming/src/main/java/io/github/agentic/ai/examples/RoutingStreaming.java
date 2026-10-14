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

import io.github.agentic.ai.graph.agent.flow.agent.LlmRoutingAgent;
import java.time.Duration;

/** Specialists describe their responsibilities so the routing model can choose one. */
public final class RoutingStreaming {
    private RoutingStreaming() {}
    public static LlmRoutingAgent router(ChatClient client) {
        var billing = ReactAgent.builder().name("billing").description("Invoices and payment questions").chatClient(client)
            .instruction("Explain billing questions without changing accounts.").outputKey("answer").build();
        var technical = ReactAgent.builder().name("technical").description("Java agent and workflow questions").chatClient(client)
            .instruction("Explain Java workflow concepts.").outputKey("answer").build();
        return LlmRoutingAgent.builder().name("helpdesk").chatClient(client).subAgents(List.of(billing, technical)).fallbackAgent("technical").build();
    }
    public static void main(String[] args) throws Exception {
        router(SpringAiExample.client(System.getenv())).streamMessages("How can a paused workflow resume?")
            .doOnNext(message -> org.slf4j.LoggerFactory.getLogger(RoutingStreaming.class).info("{}: {}", message.role(), message.text()))
            .blockLast(Duration.ofSeconds(90));
    }
}
