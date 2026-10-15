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

import io.github.agentic.ai.graph.execution.run.RunCommit;
import io.github.agentic.ai.graph.execution.run.RunEntry;
import io.github.agentic.ai.graph.execution.run.RunPayload;
import io.github.agentic.ai.graph.execution.run.RunSnapshot;
import io.github.agentic.ai.graph.execution.run.RunSpecCodec;
import io.github.agentic.ai.graph.execution.run.RunState;
import io.github.agentic.ai.graph.execution.run.RunStore;
import io.github.agentic.ai.graph.execution.run.RunStoreConflictException;
import io.github.agentic.ai.graph.execution.run.TaskSpec;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/** Commits a stored review lifecycle; this class is not a task scheduler or durable backend. */
public final class StoredRunWorkflow {
    private StoredRunWorkflow() {}

    public record Result(RunSnapshot completed, boolean duplicateReturnedOriginal,
                         boolean oldOwnerRejected, boolean sessionReleased) {}

    public static Result exercise(RunStore store, Clock clock) {
        var context = RunExampleContext.authenticated("invoice-run", clock);
        var spec = RunExampleContext.specification(context, "review-session", "submit-invoice-42");
        var codec = new RunSpecCodec();
        var admitted = store.submit(codec.decodeRun(codec.encodeRun(spec)), context);
        var retryContext = RunExampleContext.authenticated("retried-request", clock);
        var retried = store.submit(RunExampleContext.specification(retryContext,
            "review-session", "submit-invoice-42"), retryContext);
        boolean deduplicated = admitted.equals(retried);

        var lease = store.acquire(spec.namespace(), spec.runId(), UUID.randomUUID(), Duration.ofSeconds(30));
        var task = new TaskSpec(TaskSpec.CURRENT_SCHEMA_VERSION, "review-task", spec.runId(),
            "invoice.review", "1", spec.input(), context.toReference());
        var running = store.commit(spec.namespace(), spec.runId(), lease,
            new RunCommit(admitted.revision(), RunState.RUNNING,
                new RunPayload(1, "invoice.checkpoint", "{\"next\":\"review\"}"),
                List.of(new RunCommit.TaskUpdate(task, -1, RunState.PENDING)), List.of(), List.of()));
        var waiting = store.commit(spec.namespace(), spec.runId(), lease,
            new RunCommit(running.revision(), RunState.WAITING, null,
                List.of(new RunCommit.TaskUpdate(task, 0, RunState.RUNNING)), List.of(), List.of()));

        // Releasing physical ownership does not release the logical session reservation.
        store.release(lease);
        var replacement = store.acquire(spec.namespace(), spec.runId(), UUID.randomUUID(), Duration.ofSeconds(30));
        replacement = store.renew(replacement, Duration.ofSeconds(45));
        boolean oldOwnerRejected = false;
        try {
            store.commit(spec.namespace(), spec.runId(), lease,
                new RunCommit(waiting.revision(), RunState.RUNNING, null, List.of(), List.of(), List.of()));
        } catch (RunStoreConflictException expected) {
            oldOwnerRejected = true;
        }
        var resumed = store.commit(spec.namespace(), spec.runId(), replacement,
            new RunCommit(waiting.revision(), RunState.RUNNING, null, List.of(), List.of(), List.of()));

        // The approval is explicit example input, not a claim that a task executor ran.
        var approval = new RunEntry(1, "approval-42", RunEntry.Kind.APPROVAL,
            new RunPayload(1, "invoice.approval", "{\"approved\":true}"));
        var event = new RunEntry(1, "review-completed-42", RunEntry.Kind.EVENT,
            new RunPayload(1, "invoice.reviewed", "{\"invoice\":\"INV-42\",\"approved\":true}"));
        var completed = store.commit(spec.namespace(), spec.runId(), replacement,
            new RunCommit(resumed.revision(), RunState.SUCCEEDED,
                new RunPayload(1, "invoice.checkpoint", "{\"status\":\"approved\"}"),
                List.of(new RunCommit.TaskUpdate(task, 1, RunState.SUCCEEDED)),
                List.of(approval), List.of(event)));
        return new Result(completed, deduplicated, oldOwnerRejected, store.activeRun(spec.namespace()).isEmpty());
    }
}
