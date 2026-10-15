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

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DynamicApprovalTest {
    @Test void demonstratesTheDocumentedContract() throws Exception {
        var graph = DynamicApproval.workflow();
        var config = io.github.agentic.ai.graph.RunnableConfig.builder().threadId("test").build();
        var events = graph.stream(Map.of(), config).collectList().block();
        assertTrue(events.stream().anyMatch(e -> e instanceof io.github.agentic.ai.graph.action.InterruptionMetadata));
        var resume = graph.updateState(config, Map.of("approved", false)).withResume();
        assertEquals("rejected", graph.invoke((Map<String, Object>) null, resume).orElseThrow().value("status").orElseThrow());
        DynamicApproval.main(new String[0]);
    }
}
