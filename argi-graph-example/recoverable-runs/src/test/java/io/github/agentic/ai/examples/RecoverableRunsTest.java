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

import io.github.agentic.ai.graph.execution.run.MemoryRunStore;
import io.github.agentic.ai.graph.execution.run.RunSpecCodec;
import io.github.agentic.ai.graph.execution.run.RunState;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecoverableRunsTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-15T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void commitsRunTaskCheckpointJournalAndOutboxBeforeReleasingTheSession() {
        var store = new MemoryRunStore(clock);
        var result = StoredRunWorkflow.exercise(store, clock);
        assertTrue(result.duplicateReturnedOriginal());
        assertTrue(result.oldOwnerRejected());
        assertTrue(result.sessionReleased());
        assertEquals(RunState.SUCCEEDED, result.completed().state());
        assertEquals(4, result.completed().revision());
        assertEquals(RunState.SUCCEEDED, result.completed().tasks().get("review-task").state());
        assertEquals(2, result.completed().tasks().get("review-task").revision());
        assertEquals("{\"status\":\"approved\"}", result.completed().checkpoint().json());
        assertTrue(result.completed().journal().containsKey("approval-42"));
        assertTrue(result.completed().outbox().containsKey("review-completed-42"));
        assertFalse(store.capabilities().supportsDurableMode());
        assertThrows(IllegalStateException.class, () -> store.capabilities().requireDurableMode());
    }

    @Test
    void rejectsStaleRevisionsConcurrentOwnersAndInvalidAtomicWrites() {
        assertEquals(4, RunConflictScenarios.exercise(clock));
    }

    @Test
    void serializesDataButRequiresALiveContextAtAdmission() {
        var context = RunExampleContext.authenticated("codec-run", clock);
        var spec = RunExampleContext.specification(context, "session", "request");
        var codec = new RunSpecCodec();
        var restored = codec.decodeRun(codec.encodeRun(spec));
        assertEquals(spec, restored);
        assertEquals(spec.inputHash(), restored.input().inputHash());
        assertThrows(NullPointerException.class, () -> new MemoryRunStore(clock).submit(restored, null));
    }
}
