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
import io.github.agentic.ai.graph.CompileConfig;
import io.github.agentic.ai.graph.CompiledGraph;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.checkpoint.config.SaverConfig;
import io.github.agentic.ai.graph.checkpoint.savers.MemorySaver;
import java.util.Locale;
import java.util.Map;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Classifies an issue, drafts a response, then requires review before publication. */
public final class SupportWorkflow {

	private SupportWorkflow() {
	}

	public static CompiledGraph workflow(ChatClient client) throws Exception {
		var writer = ReactAgent.builder()
			.throwOnModelError(true)
			.name("draft")
			.chatClient(client)
			.outputKey("draft")
			.instruction("Draft a concise support reply for this ticket: {ticket}. Category: {category}.")
			.build();
		return new StateGraph()
			.addNode("classify",
					node_async(s -> Map.of("category",
							s.<String>value("ticket").orElseThrow().toLowerCase(Locale.ROOT).contains("refund")
									? "billing" : "technical")))
			.addNode("draft", writer.asNode(true, false))
			.addNode("review", node_async(
					s -> Map.of("status", s.<Boolean>value("approved").orElse(false) ? "ready-to-send" : "rejected")))
			.addEdge(START, "classify")
			.addEdge("classify", "draft")
			.addEdge("draft", "review")
			.addEdge("review", END)
			.compile(CompileConfig.builder()
				.saverConfig(SaverConfig.builder().register(new MemorySaver()).build())
				.interruptBefore("review")
				.build());
	}

	public static void main(String[] args) throws Exception {
		var graph = workflow(SpringAiExample.client(System.getenv()));
		var config = RunnableConfig.builder().threadId("ticket-42").build();
		graph.invoke(Map.of("ticket", "Please explain how to request a refund."), config);
		var resume = graph.updateState(config, Map.of("approved", true)).withResume();
		org.slf4j.LoggerFactory.getLogger(SupportWorkflow.class)
			.info("{}", graph.invoke((Map<String, Object>) null, resume).orElseThrow().data());
	}

}
