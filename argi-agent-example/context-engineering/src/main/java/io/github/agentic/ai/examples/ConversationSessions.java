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
import java.util.Map;
import org.slf4j.LoggerFactory;

/** Reuses one agent while keeping each customer's checkpoint history separate. */
public final class ConversationSessions {

	private final ReactAgent agent;

	public ConversationSessions(ChatClient client) {
		agent = ContextEngineering.agent(client);
	}

	public String ask(String thread, String prompt) throws Exception {
		if (thread == null || thread.isBlank()) {
			throw new IllegalArgumentException("A stable conversation thread is required");
		}
		return agent
			.call(prompt, RunnableConfig.builder().threadId(thread).addMetadata("channel", "support-example").build())
			.text();
	}

	public static void main(String[] args) throws Exception {
		var sessions = new ConversationSessions(SpringAiExample.client(System.getenv()));
		var customers = Map.of("customer-ada", "ORDER-42", "customer-grace", "ORDER-43");
		var log = LoggerFactory.getLogger(ConversationSessions.class);
		for (var customer : customers.entrySet()) {
			sessions.ask(customer.getKey(), "My order number is " + customer.getValue() + ".");
		}
		for (String thread : customers.keySet()) {
			log.info("thread={} remembered={}", thread, sessions.ask(thread, "What is my order number?"));
		}
		// Each thread consumed its configured two model calls. A third request should
		// fail.
		try {
			sessions.ask("customer-ada", "Explain checkpointing.");
			throw new IllegalStateException("The configured thread budget was not enforced");
		}
		catch (io.github.agentic.ai.graph.agent.hook.modelcalllimit.ModelCallLimitExceededException expected) {
			log.info("Thread budget stopped a third request: {}", expected.getMessage());
		}
	}

}
