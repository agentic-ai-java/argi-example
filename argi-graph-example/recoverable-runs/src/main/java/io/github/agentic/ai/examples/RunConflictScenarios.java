/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.agentic.ai.examples;

import io.github.agentic.ai.graph.execution.run.MemoryRunStore;
import io.github.agentic.ai.graph.execution.run.RunCommit;
import io.github.agentic.ai.graph.execution.run.RunEntry;
import io.github.agentic.ai.graph.execution.run.RunPayload;
import io.github.agentic.ai.graph.execution.run.RunSpec;
import io.github.agentic.ai.graph.execution.run.RunState;
import io.github.agentic.ai.graph.execution.run.RunStoreConflictException;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates deduplication conflicts, stale revisions and all-or-nothing validation.
 */
public final class RunConflictScenarios {

	private RunConflictScenarios() {
	}

	public static int exercise(Clock clock) {
		var store = new MemoryRunStore(clock);
		var context = RunExampleContext.authenticated("conflict-run", clock);
		var spec = RunExampleContext.specification(context, "conflict-session", "same-request");
		store.submit(spec, context);
		var changed = RunSpec.from(context, "argi-example", "conflict-session", "same-request", "invoice-review", "1",
				new RunPayload(1, "invoice.input", "{\"amount\":84}"));
		int rejected = expectConflict(() -> store.submit(changed, context));

		var lease = store.acquire(spec.namespace(), spec.runId(), UUID.randomUUID(), Duration.ofSeconds(30));
		rejected += expectConflict(
				() -> store.acquire(spec.namespace(), spec.runId(), UUID.randomUUID(), Duration.ofSeconds(30)));
		var running = store.commit(spec.namespace(), spec.runId(), lease,
				new RunCommit(0, RunState.RUNNING, null, List.of(), List.of(), List.of()));
		rejected += expectConflict(() -> store.commit(spec.namespace(), spec.runId(), lease,
				new RunCommit(0, RunState.WAITING, null, List.of(), List.of(), List.of())));

		// A journal-only kind cannot enter the outbox; the checkpoint must not be
		// partially published.
		var invalidEvent = new RunEntry(1, "invalid-outbox", RunEntry.Kind.APPROVAL,
				new RunPayload(1, "approval", "{\"approved\":true}"));
		try {
			store.commit(spec.namespace(), spec.runId(), lease,
					new RunCommit(running.revision(), RunState.WAITING,
							new RunPayload(1, "checkpoint", "{\"mustNotPersist\":true}"), List.of(), List.of(),
							List.of(invalidEvent)));
			throw new IllegalStateException("Invalid outbox entry was accepted");
		}
		catch (IllegalArgumentException expected) {
			rejected++;
		}
		if (!running.equals(store.find(spec.namespace(), spec.runId()).orElseThrow())) {
			throw new IllegalStateException("A rejected commit changed stored state");
		}
		store.commit(spec.namespace(), spec.runId(), lease,
				new RunCommit(running.revision(), RunState.CANCELLED, null, List.of(), List.of(), List.of()));
		return rejected;
	}

	private static int expectConflict(Runnable operation) {
		try {
			operation.run();
			throw new IllegalStateException("Expected a run-store conflict");
		}
		catch (RunStoreConflictException expected) {
			return 1;
		}
	}

	public static void main(String[] args) {
		LoggerFactory.getLogger(RunConflictScenarios.class)
			.info("Rejected invalid operations: {}", exercise(Clock.systemUTC()));
	}

}
