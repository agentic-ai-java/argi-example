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

import io.github.agentic.ai.graph.RunnableConfig;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CheckpointInspectorTest {
    @TempDir
    Path directory;

    @Test
    void restoresEditedCheckpointWithoutResumingAnotherThread() throws Exception {
        var original = Persistence.workflow(directory);
        var first = RunnableConfig.builder().threadId("first").build();
        var second = RunnableConfig.builder().threadId("second").build();
        original.invoke(Map.of("invoice", "first", "amount", 42), first);
        original.invoke(Map.of("invoice", "second", "amount", 84), second);
        var reopened = Persistence.workflow(directory);
        assertFalse(CheckpointInspector.history(reopened, first).isEmpty());
        assertTrue(CheckpointInspector.history(reopened, first).stream()
            .allMatch(view -> "first".equals(view.data().get("invoice"))));
        var cursor = reopened.updateState(first, Map.of("amount", 40)).withResume();
        var complete = reopened.invoke((Map<String, Object>) null, cursor).orElseThrow();
        assertEquals(40, complete.value("amount").orElseThrow());
        assertEquals("complete", complete.value("status").orElseThrow());
        assertEquals("pending", reopened.getState(second).state().value("status").orElseThrow());
    }
}
