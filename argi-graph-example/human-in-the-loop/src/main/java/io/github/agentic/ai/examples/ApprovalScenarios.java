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
import java.util.Map;
import org.slf4j.LoggerFactory;

/** Resolves two independent pending requests with different human decisions. */
public final class ApprovalScenarios {

	private ApprovalScenarios() {
	}

	public static void main(String[] args) throws Exception {
		var graph = HumanInTheLoop.workflow();
		var accepted = RunnableConfig.builder().threadId("approved-refund").build();
		var rejected = RunnableConfig.builder().threadId("rejected-refund").build();
		graph.invoke(Map.of("amount", 42), accepted);
		graph.invoke(Map.of("amount", 84), rejected);
		var log = LoggerFactory.getLogger(ApprovalScenarios.class);
		log.info("Pending node: {}", graph.getState(accepted).next());
		for (var entry : Map.of(accepted, true, rejected, false).entrySet()) {
			var cursor = graph.updateState(entry.getKey(), Map.of("approved", entry.getValue())).withResume();
			var state = graph.invoke((Map<String, Object>) null, cursor).orElseThrow();
			log.info("decision={} finalState={}", entry.getValue(), state.data());
		}
	}

}
