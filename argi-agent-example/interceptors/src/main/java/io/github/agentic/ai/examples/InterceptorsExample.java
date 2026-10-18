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
import io.github.agentic.ai.api.tool.ToolRegistry;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.interceptor.ModelCallHandler;
import io.github.agentic.ai.graph.agent.interceptor.ModelInterceptor;
import io.github.agentic.ai.graph.agent.interceptor.ModelRequest;
import io.github.agentic.ai.graph.agent.interceptor.ModelResponse;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallHandler;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallRequest;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallResponse;
import io.github.agentic.ai.graph.agent.interceptor.ToolInterceptor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches only the public tracking fixture; invocation IDs are rebuilt for each caller.
 */
public final class InterceptorsExample {

	private InterceptorsExample() {
	}

	public static final class Timing extends ModelInterceptor {

		@Override
		public String getName() {
			return "model_timing";
		}

		@Override
		public ModelResponse interceptModel(ModelRequest request, ModelCallHandler handler) {
			long started = System.nanoTime();
			try {
				return handler.call(request);
			}
			finally {
				org.slf4j.LoggerFactory.getLogger(Timing.class)
					.info("Model duration: {} ms", (System.nanoTime() - started) / 1_000_000);
			}
		}

	}

	public static final class TrackingCache extends ToolInterceptor {

		private final Map<String, String> cache = new ConcurrentHashMap<>();

		@Override
		public String getName() {
			return "tracking_cache";
		}

		@Override
		public ToolCallResponse interceptToolCall(ToolCallRequest request, ToolCallHandler handler) {
			if (!request.getToolName().equals("track_order"))
				return handler.call(request);
			String cached = cache.get(request.getArguments());
			if (cached != null)
				return ToolCallResponse.success(request.getToolCallId(), request.getToolName(), cached);
			var response = handler.call(request);
			if (!response.isError())
				cache.put(request.getArguments(), response.getResult());
			return response;
		}

	}

	public static ReactAgent agent(ChatClient client) {
		return ReactAgent.builder()
			.throwOnModelError(true)
			.name("instrumented_delivery")
			.chatClient(client)
			.toolRegistry(new ToolRegistry(List.of(ReactTools.trackingTool())))
			.interceptors(List.of(new Timing(), new TrackingCache()))
			.build();
	}

	public static void main(String[] args) throws Exception {
		org.slf4j.LoggerFactory.getLogger(InterceptorsExample.class)
			.info("{}", agent(SpringAiExample.client(System.getenv())).call("Track ORDER-42.").text());
	}

}
