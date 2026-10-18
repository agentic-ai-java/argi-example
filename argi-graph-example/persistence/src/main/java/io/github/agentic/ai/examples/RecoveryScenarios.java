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

import io.github.agentic.ai.graph.RunnableConfig;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.slf4j.LoggerFactory;

/**
 * Reopens the saver, edits a suspended state and proves that another thread stays paused.
 */
public final class RecoveryScenarios {

	private RecoveryScenarios() {
	}

	public static void main(String[] args) throws Exception {
		Path directory = Path.of("target", "recovery-scenarios", UUID.randomUUID().toString());
		var first = Persistence.workflow(directory);
		var invoiceA = RunnableConfig.builder().threadId("invoice-a").build();
		var invoiceB = RunnableConfig.builder().threadId("invoice-b").build();
		first.invoke(Map.of("invoice", "INV-A", "amount", 42), invoiceA);
		first.invoke(Map.of("invoice", "INV-B", "amount", 84), invoiceB);

		// A fresh compiled graph has no reference to the original graph's in-memory
		// state.
		var recovered = Persistence.workflow(directory);
		var log = LoggerFactory.getLogger(RecoveryScenarios.class);
		for (var checkpoint : CheckpointInspector.history(recovered, invoiceA)) {
			log.info("node={} next={} cursor={} state={}", checkpoint.node(), checkpoint.next(), checkpoint.cursor(),
					checkpoint.data());
		}
		var cursor = recovered.updateState(invoiceA, Map.of("amount", 40, "reviewed", true));
		var completed = recovered.invoke((Map<String, Object>) null, cursor.withResume()).orElseThrow();
		log.info("Recovered and edited invoice: {}", completed.data());
		log.info("Other thread is still pending: {}", recovered.getState(invoiceB).state().data());
		log.info("Files retained for inspection: {}", directory.toAbsolutePath());
	}

}
