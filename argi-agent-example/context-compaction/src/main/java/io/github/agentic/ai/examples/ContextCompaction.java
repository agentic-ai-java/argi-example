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

import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.hook.summarization.SummarizationHook;
import io.github.agentic.ai.graph.checkpoint.savers.MemorySaver;
import java.util.List;

/** Summaries and recent messages remain in the same saved conversation thread. */
public final class ContextCompaction {

	private ContextCompaction() {
	}

	public static ReactAgent agent(ChatClient client) {
		var summarizer = SummarizationHook.builder()
			.chatClient(client)
			.maxTokensBeforeSummary(1500)
			.messagesToKeep(4)
			.keepFirstUserMessage(true)
			.build();
		return ReactAgent.builder()
			.throwOnModelError(true)
			.name("project_assistant")
			.chatClient(client)
			.saver(new MemorySaver())
			.hooks(List.of(summarizer))
			.instruction("Track decisions and open questions about the project.")
			.build();
	}

	public static void main(String[] args) throws Exception {
		var agent = agent(SpringAiExample.client(System.getenv()));
		var config = RunnableConfig.builder().threadId("project-argi").build();
		for (String input : List.of("We need durable checkpoint recovery.", "Choose JDBC for local relational storage.",
				"Keep approval decisions in the saved thread.", "List our decisions and remaining questions.")) {
			org.slf4j.LoggerFactory.getLogger(ContextCompaction.class).info("{}", agent.call(input, config).text());
		}
	}

}
