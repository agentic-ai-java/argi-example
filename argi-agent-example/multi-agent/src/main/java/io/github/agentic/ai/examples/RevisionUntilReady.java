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
import io.github.agentic.ai.api.chat.ChatRole;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.flow.agent.LoopAgent;
import io.github.agentic.ai.graph.agent.flow.agent.loop.ConditionLoopStrategy;
import org.slf4j.LoggerFactory;

/** Stops on an explicit review marker, with a three-iteration ceiling. */
public final class RevisionUntilReady {
    private RevisionUntilReady() {}

    public static LoopAgent workflow(ChatClient client) {
        var writer = ReactAgent.builder().throwOnModelError(true).name("revision_writer").chatClient(client)
            .instruction("Revise the release checklist using prior feedback. Include recovery, rollback, "
                + "and review ownership. End with [READY] only when all three are covered.")
            .outputKey("revision").build();
        var strategy = new ConditionLoopStrategy(messages -> messages.stream()
            .filter(message -> message.role() == ChatRole.ASSISTANT)
            .reduce((first, second) -> second).map(message -> message.text().endsWith("[READY]")).orElse(false)) {
            @Override
            public int maxLoopCount() {
                return 3;
            }
        };
        return LoopAgent.builder().name("bounded_revision").subAgent(writer).loopStrategy(strategy).build();
    }

    public static void main(String[] args) throws Exception {
        var result = workflow(SpringAiExample.client(System.getenv()))
            .invoke("Draft a release checklist for checkpoint recovery.").orElseThrow();
        LoggerFactory.getLogger(RevisionUntilReady.class).info("Final revision: {}", result.value("revision"));
    }
}
