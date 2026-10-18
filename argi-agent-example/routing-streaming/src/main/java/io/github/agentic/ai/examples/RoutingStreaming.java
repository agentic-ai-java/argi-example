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
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.flow.agent.LlmRoutingAgent;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/** Specialists describe their responsibilities so the routing model can choose one. */
public final class RoutingStreaming {

	private RoutingStreaming() {
	}

	public static LlmRoutingAgent router(ChatClient client) {
		return router(client, ForkJoinPool.commonPool());
	}

	public static LlmRoutingAgent router(ChatClient client, Executor executor) {
		var billing = ReactAgent.builder()
			.throwOnModelError(true)
			.name("billing")
			.description("Invoices and payment questions")
			.chatClient(client)
			.instruction("Explain billing questions without changing accounts.")
			.outputKey("answer")
			.build();
		var technical = ReactAgent.builder()
			.throwOnModelError(true)
			.name("technical")
			.description("Java agent and workflow questions")
			.chatClient(client)
			.instruction("Explain Java workflow concepts.")
			.outputKey("answer")
			.build();
		return LlmRoutingAgent.builder()
			.name("helpdesk")
			.executor(executor)
			.chatClient(client)
			.subAgents(List.of(billing, technical))
			.fallbackAgent("technical")
			.build();
	}

	public static void main(String[] args) throws Exception {
		var workers = Executors.newFixedThreadPool(2);
		try {
			router(SpringAiExample.client(System.getenv()), workers).streamMessages("How can a paused workflow resume?")
				.filter(message -> message.text() != null && !message.text().isBlank())
				.doOnNext(message -> org.slf4j.LoggerFactory.getLogger(RoutingStreaming.class)
					.info("{}: {}", message.role(), message.text()))
				.blockLast(Duration.ofSeconds(180));
		}
		finally {
			workers.shutdownNow();
		}
	}

}
