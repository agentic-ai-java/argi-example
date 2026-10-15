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

import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.graph.OverAllState;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ReviewMergeStrategyTest {
    @Test
    void mergesInStableOrderAndProjectsNeutralMessagesToText() {
        var result = new ReviewMergeStrategy().merge(Map.of(
            "operations", ChatMessage.assistant("Close executors"),
            "correctness", "Preserve approval checkpoints"), new OverAllState());
        assertEquals(List.of(
            Map.of("reviewer", "correctness", "review", "Preserve approval checkpoints"),
            Map.of("reviewer", "operations", "review", "Close executors")), result);
    }
}
