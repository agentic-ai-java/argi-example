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

import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.api.tool.ToolContext;
import io.github.agentic.ai.api.tool.ToolRegistry;
import java.util.List;
import java.util.concurrent.CompletionException;
import org.slf4j.LoggerFactory;

/** Exercises successful calls, invalid arguments, missing data and unknown tool names. */
public final class ToolScenarios {

	private ToolScenarios() {
	}

	public static void main(String[] args) {
		var registry = new ToolRegistry(List.of(new TrackingTool(OrderRepository.sample())));
		var log = LoggerFactory.getLogger(ToolScenarios.class);
		for (var call : List.of(new ToolCall("found", "track_order", "{\"orderId\":\"ORDER-42\"}"),
				new ToolCall("missing", "track_order", "{\"orderId\":\"ORDER-404\"}"),
				new ToolCall("invalid", "track_order", "{\"orderId\":42}"),
				new ToolCall("malformed", "track_order", "{"))) {
			var result = registry.execute(call, ToolContext.empty()).toCompletableFuture().join();
			var message = result.toMessage(call);
			log.info("call={} tool={} error={} result={}", message.toolCallId(), message.toolName(), result.error(),
					message.text());
		}
		try {
			registry.execute(new ToolCall("unknown", "missing_tool", "{}"), ToolContext.empty())
				.toCompletableFuture()
				.join();
			throw new IllegalStateException("An unknown tool must fail before execution");
		}
		catch (CompletionException expected) {
			if (!(expected.getCause() instanceof IllegalArgumentException)) {
				throw expected;
			}
			log.info("Unknown tool rejected: {}", expected.getCause().getMessage());
		}
	}

}
