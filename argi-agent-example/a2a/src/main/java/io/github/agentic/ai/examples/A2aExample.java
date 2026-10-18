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

import io.github.agentic.ai.graph.agent.a2a.A2aRemoteAgent;
import io.github.agentic.ai.graph.agent.a2a.RemoteAgentCardProvider;
import java.util.Objects;

/** Requires an existing A2A service exposing a public, unauthenticated agent card. */
public final class A2aExample {

	private A2aExample() {
	}

	public static A2aRemoteAgent remote(String baseUrl) {
		return A2aRemoteAgent.builder()
			.name("remote_research")
			.agentCardProvider(RemoteAgentCardProvider.newProvider(baseUrl))
			.description("Remote research service")
			.outputKey("research")
			.streaming(false)
			.build();
	}

	public static void main(String[] args) throws Exception {
		String url = Objects.requireNonNull(System.getenv("A2A_BASE_URL"), "Set A2A_BASE_URL");
		org.slf4j.LoggerFactory.getLogger(A2aExample.class)
			.info("{}", remote(url).invoke("Explain durable workflow recovery.").orElseThrow().data());
	}

}
