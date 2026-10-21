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

import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.node.network.HttpNode;
import java.time.Duration;
import java.util.Map;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/**
 * Retrieves an HTTP resource as a graph step, with explicit network policy and timeout.
 */
public final class HttpNodeExample {

	private HttpNodeExample() {
	}

	public static Map<String, Object> fetch(String baseUrl, boolean allowPrivate) throws Exception {
		var node = HttpNode.builder()
			.url(baseUrl + "/orders/${orderId}")
			.outputKey("tracking")
			.allowPrivateNetworkAccess(allowPrivate)
			.requestTimeout(Duration.ofSeconds(5))
			.retryConfig(new HttpNode.RetryConfig(0, 1000, false))
			.build();
		var graph = new StateGraph().addNode("fetch", node_async(node))
			.addEdge(START, "fetch")
			.addEdge("fetch", END)
			.compile();
		return graph.invoke(Map.of("orderId", "ORDER-42")).orElseThrow().data();
	}

	public static void main(String[] args) throws Exception {
		String url = System.getenv().getOrDefault("TRACKING_BASE_URL", "http://127.0.0.1:8091");
		boolean allowPrivate = Boolean.parseBoolean(System.getenv().getOrDefault("ALLOW_PRIVATE_NETWORK", "false"));
		org.slf4j.LoggerFactory.getLogger(HttpNodeExample.class).info("{}", fetch(url, allowPrivate));
	}

}
