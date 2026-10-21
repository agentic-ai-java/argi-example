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
import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.graph.agent.extension.interceptor.AssistantMessageSanitizerInterceptor;
import io.github.agentic.ai.graph.agent.interceptor.ModelRequest;
import io.github.agentic.ai.graph.agent.interceptor.ModelResponse;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallRequest;
import io.github.agentic.ai.graph.agent.interceptor.toolerror.ToolErrorInterceptor;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * Inspects empty assistant-content normalization and model-visible tool exception text.
 */
public final class MessageSanitizerScenarios {

	private MessageSanitizerScenarios() {
	}

	public static void main(String[] args) {
		var call = new ToolCall("lookup-1", "track_order", "{\"orderId\":\"ORDER-42\"}");
		var request = ModelRequest.builder()
			.messages(List.of(ChatMessage.user("Track my order"), ChatMessage.assistant(null, List.of(call)),
					ChatMessage.tool(call.id(), call.name(), "Dispatched")))
			.build();
		var sanitizer = AssistantMessageSanitizerInterceptor.builder().build();
		sanitizer.interceptModel(request, sanitized -> {
			var assistant = sanitized.getMessages().get(1);
			if (assistant.content().isEmpty() || !assistant.toolCalls().equals(List.of(call))) {
				throw new IllegalStateException("Sanitization lost the tool invocation or empty text content");
			}
			return ModelResponse.of(ChatMessage.assistant("Tool message structure preserved."));
		});
		var error = ToolErrorInterceptor.builder()
			.build()
			.interceptToolCall(ToolCallRequest.builder().toolCall(call).build(), ignored -> {
				throw new IllegalStateException("Injected read failure");
			});
		// RC2 ToolErrorInterceptor exposes text using of(); it does not set an error
		// status.
		LoggerFactory.getLogger(MessageSanitizerScenarios.class)
			.info("call={} result={} errorFlag={}", error.getToolCallId(), error.getResult(), error.isError());
	}

}
