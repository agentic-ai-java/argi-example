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
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.agent.hook.TokenCounter;
import java.util.List;
import org.slf4j.LoggerFactory;

/** Supplies a long transcript and checks the actual summary marker in saved state. */
public final class CompactionScenarios {

	private CompactionScenarios() {
	}

	public static void main(String[] args) throws Exception {
		var input = ProjectTranscript.messages();
		var log = LoggerFactory.getLogger(CompactionScenarios.class);
		log.info("Input messages={} estimatedTokens={}", input.size(),
				TokenCounter.approximateMsgCounter().countTokens(input));
		var agent = ContextCompaction.agent(SpringAiExample.client(System.getenv()));
		var state = agent.invoke(input, RunnableConfig.builder().threadId("long-project-history").build())
			.orElseThrow();
		List<ChatMessage> retained = state.<List<ChatMessage>>value("messages").orElseThrow();
		boolean summaryPresent = retained.stream()
			.anyMatch(message -> message.text().startsWith("## Previous conversation summary:"));
		if (!summaryPresent || !retained.contains(input.get(0))) {
			throw new IllegalStateException(
					"Summarization did not preserve the expected summary and first user message");
		}
		log.info("Retained messages={} summaryPresent={} firstUserPreserved={}", retained.size(), summaryPresent,
				retained.contains(input.get(0)));
	}

}
