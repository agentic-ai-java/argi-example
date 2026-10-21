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
import org.slf4j.LoggerFactory;

/**
 * Demonstrates ReactAgent's outputType in addition to the lower-level response format.
 */
public final class TypedTicketAgent {

	private TypedTicketAgent() {
	}

	public static ReactAgent agent(ChatClient client) {
		return ReactAgent.builder()
			.throwOnModelError(true)
			.name("typed_ticket_classifier")
			.chatClient(client)
			.outputType(StructuredOutput.Ticket.class)
			.instruction("Classify support tickets. Category must be billing, technical or general. "
					+ "Priority is an integer from 1 to 5. Return only the requested JSON object.")
			.build();
	}

	public static void main(String[] args) throws Exception {
		var result = agent(SpringAiExample.client(System.getenv()))
			.call(args.length == 0 ? "I was charged twice." : String.join(" ", args));
		// A generated schema does not replace application domain validation.
		LoggerFactory.getLogger(TypedTicketAgent.class).info("Validated ticket: {}", TicketSchema.parse(result.text()));
	}

}
