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

import io.github.agentic.ai.graph.OverAllState;
import io.github.agentic.ai.graph.agent.flow.agent.SequentialAgent;
import io.github.agentic.ai.graph.agent.flow.agent.ParallelAgent;
import io.github.agentic.ai.graph.agent.flow.agent.LoopAgent;
import io.github.agentic.ai.graph.agent.flow.agent.loop.LoopMode;

/** Independent reviewers feed a writer, followed by a bounded revision loop. */
public final class MultiAgent {
    private MultiAgent() {}

    public static SequentialAgent workflow(ChatClient client) {
        var technical = ReactAgent.builder().name("technical").chatClient(client)
            .instruction("Review the technical risks of the proposal.").outputKey("technical_review").build();
        var usability = ReactAgent.builder().name("usability").chatClient(client)
            .instruction("Review the user experience risks of the proposal.").outputKey("usability_review").build();
        var reviewers = ParallelAgent.builder().name("reviewers").subAgents(List.of(technical, usability)).build();
        var writer = ReactAgent.builder().name("writer").chatClient(client)
            .instruction("Write a release checklist using these reviews: {technical_review}; {usability_review}.")
            .outputKey("checklist").build();
        var revisions = LoopAgent.builder().name("revisions").subAgent(writer).loopStrategy(LoopMode.count(2)).build();
        return SequentialAgent.builder().name("release_review").subAgents(List.of(reviewers, revisions)).build();
    }
    public static void main(String[] args) throws Exception {
        var result = workflow(SpringAiExample.client(System.getenv())).invoke("Add checkpoint recovery to a support agent.").orElseThrow();
        org.slf4j.LoggerFactory.getLogger(MultiAgent.class).info("{}", result.value("checklist").orElseThrow());
    }
}
