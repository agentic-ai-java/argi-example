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
import java.time.Clock;
import org.slf4j.LoggerFactory;

/**
 * Entrypoint for opt-in run/task storage contracts added after the original example
 * baseline.
 */
public final class RecoverableRuns {

	private RecoverableRuns() {
	}

	public static void main(String[] args) {
		var clock = Clock.systemUTC();
		var store = new MemoryRunStore(clock);
		var log = LoggerFactory.getLogger(RecoverableRuns.class);
		log.info("Restart durability supported: {}", store.capabilities().supportsDurableMode());
		var result = StoredRunWorkflow.exercise(store, clock);
		log.info("Deduplicated={} oldOwnerRejected={} sessionReleased={}", result.duplicateReturnedOriginal(),
				result.oldOwnerRejected(), result.sessionReleased());
		log.info("Run={} revision={} task={} journal={} outbox={}", result.completed().state(),
				result.completed().revision(), result.completed().tasks(), result.completed().journal(),
				result.completed().outbox());
		log.info("Conflict scenarios rejected: {}", RunConflictScenarios.exercise(clock));
	}

}
