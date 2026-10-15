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

class HumanInTheLoopTest {
    @Test void exercisesWorkflow() throws Exception {
        var graph = HumanInTheLoop.workflow();
        var config = io.github.agentic.ai.graph.RunnableConfig.builder().threadId("pending").build();
        graph.invoke(Map.of("amount", 10), config);
        assertEquals("awaiting-approval", graph.getState(config).state().value("status").orElseThrow());
        assertEquals("refunded", HumanInTheLoop.refund(true));
        assertEquals("rejected", HumanInTheLoop.refund(false));
        HumanInTheLoop.main(new String[0]);
    }
}
