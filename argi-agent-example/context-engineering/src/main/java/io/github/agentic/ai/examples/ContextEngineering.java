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
import io.github.agentic.ai.graph.agent.ReactAgent;
import java.util.List;
import java.util.Map;

import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.checkpoint.savers.MemorySaver;
import io.github.agentic.ai.graph.agent.hook.modelcalllimit.ModelCallLimitHook;

/** Maintains per-thread history and limits the total number of model turns. */
public final class ContextEngineering {
    private ContextEngineering() {}

    public static ReactAgent agent(ChatClient client) {
        var budget = ModelCallLimitHook.builder().threadLimit(2).runLimit(1)
            .exitBehavior(ModelCallLimitHook.ExitBehavior.ERROR).build();
        return ReactAgent.builder().name("support_session").chatClient(client).saver(new MemorySaver())
            .instruction("Use the conversation history to answer briefly.").hooks(List.of(budget)).build();
    }
    public static List<String> conversation(ChatClient client) throws Exception {
        var agent = agent(client);
        var config = RunnableConfig.builder().threadId("customer-42").build();
        String first = agent.call("My order number is ORDER-42.", config).text();
        String second = agent.call("What is my order number?", config).text();
        return List.of(first, second);
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(ContextEngineering.class).info("{}", conversation(SpringAiExample.client(System.getenv())));
    }
}
