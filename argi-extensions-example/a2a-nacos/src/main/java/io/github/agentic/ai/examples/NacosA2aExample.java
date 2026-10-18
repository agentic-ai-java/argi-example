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

import io.github.agentic.ai.a2a.registry.nacos.discovery.NacosAgentCardProvider;
import io.github.agentic.ai.graph.agent.a2a.A2aRemoteAgent;
import java.util.Objects;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/** Discovery and protocol execution remain separate steps. */
@SpringBootApplication
public class NacosA2aExample {

	@Bean
	ApplicationRunner delegate(NacosAgentCardProvider provider) {
		return args -> {
			String name = Objects.requireNonNull(System.getenv("A2A_AGENT_NAME"), "Set A2A_AGENT_NAME");
			provider.getAgentCard(name);
			var remote = A2aRemoteAgent.builder()
				.name(name)
				.agentCardProvider(provider)
				.outputKey("result")
				.streaming(false)
				.build();
			org.slf4j.LoggerFactory.getLogger(NacosA2aExample.class)
				.info("{}", remote.invoke("Explain checkpoint recovery.").orElseThrow().data());
		};
	}

	public static void main(String[] args) {
		try (var context = new SpringApplicationBuilder(NacosA2aExample.class).web(WebApplicationType.NONE).run(args)) {
			org.slf4j.LoggerFactory.getLogger(NacosA2aExample.class).info("Remote agent request completed");
		}
	}

}
