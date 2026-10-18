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

import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.graph.OverAllState;
import io.github.agentic.ai.graph.agent.flow.agent.ParallelAgent;
import java.util.List;
import java.util.Map;

/** Preserves each review's origin and gives the writer deterministic merge ordering. */
public final class ReviewMergeStrategy implements ParallelAgent.MergeStrategy {

	@Override
	public List<Map<String, String>> merge(Map<String, Object> results, OverAllState state) {
		return results.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByKey())
			.map(entry -> Map.of("reviewer", entry.getKey(), "review", text(entry.getValue())))
			.toList();
	}

	private static String text(Object result) {
		if (result instanceof ChatMessage message) {
			return message.text();
		}
		return String.valueOf(result);
	}

}
