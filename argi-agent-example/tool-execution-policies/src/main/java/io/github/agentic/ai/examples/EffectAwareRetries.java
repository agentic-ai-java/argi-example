/*
 * Copyright 2025-2026 the original author or authors.
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

import io.github.agentic.ai.graph.agent.interceptor.ToolCallRequest;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallResponse;
import io.github.agentic.ai.graph.agent.interceptor.toolretry.EffectAwareToolRetryInterceptor;
import io.github.agentic.ai.graph.agent.interceptor.toolretry.ToolEffect;
import io.github.agentic.ai.graph.agent.interceptor.toolretry.ToolEffectRegistry;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Injects an uncertain timeout to contrast read-only retry with unsafe payment execution.
 */
public final class EffectAwareRetries {

	private static final Logger LOG = LoggerFactory.getLogger(EffectAwareRetries.class);

	private EffectAwareRetries() {
	}

	public record Result(int attempts, ToolCallResponse response) {
		@Override
		public String toString() {
			return "attempts=" + attempts + ", status=" + response.getStatus() + ", metadata=" + response.getMetadata();
		}
	}

	public static Result timeoutThenSuccess(ToolEffect effect) {
		final AtomicInteger attempts = new AtomicInteger();
		final var retry = EffectAwareToolRetryInterceptor.builder()
			.effects(ToolEffectRegistry.builder().declare("operation", effect).build())
			.maxAttempts(2)
			.initialDelay(0)
			.jitter(false)
			.onFailure(EffectAwareToolRetryInterceptor.OnFailureBehavior.RETURN_MESSAGE)
			.build();
		final ToolCallResponse response = retry.interceptToolCall(
				ToolCallRequest.builder()
					.toolCallId("attempt-1")
					.toolName("operation")
					.arguments("{}")
					.context(Map.of())
					.build(),
				request -> attempts.incrementAndGet() == 1
						? ToolCallResponse.error(request.getToolCallId(), request.getToolName(), "Injected timeout",
								Map.of(ToolCallResponse.FAILURE_KIND_METADATA_KEY,
										ToolCallResponse.FAILURE_KIND_TIMEOUT))
						: ToolCallResponse.success(request.getToolCallId(), request.getToolName(), "completed"));
		return new Result(attempts.get(), response);
	}

	public static void main(String[] args) {
		LOG.info("Read-only operation: {}", timeoutThenSuccess(ToolEffect.READ_ONLY));
		LOG.info("Unsafe operation: {}", timeoutThenSuccess(ToolEffect.UNSAFE));
	}

}
