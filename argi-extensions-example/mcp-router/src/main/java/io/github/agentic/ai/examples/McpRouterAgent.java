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

import io.github.agentic.ai.adapter.springai.SpringAiToolAdapter;
import io.github.agentic.ai.graph.agent.ReactAgent;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/** Only ARGI executes the discovered callbacks; the model receives their schemas. */
@SpringBootApplication
public class McpRouterAgent {

	@Bean
	EmbeddingModel embeddingModel() {
		return OpenAiEmbeddingModel.builder()
			.options(OpenAiEmbeddingOptions.builder()
				.apiKey(System.getenv("ARGI_API_KEY"))
				.model(System.getenv().getOrDefault("ARGI_EMBEDDING_MODEL", "text-embedding-3-small"))
				.baseUrl(System.getenv().getOrDefault("ARGI_BASE_URL", "https://api.openai.com/v1"))
				.build())
			.build();
	}

	@Bean
	ApplicationRunner ask(@Qualifier("routerTools") ToolCallbackProvider provider) {
		return args -> {
			var agent = ReactAgent.builder()
				.throwOnModelError(true)
				.name("mcp_router")
				.chatClient(SpringAiExample.client(System.getenv()))
				.toolRegistry(SpringAiToolAdapter.fromCallbacks(provider.getToolCallbacks()))
				.instruction("Use the available read-only catalog tools to answer the question.")
				.build();
			org.slf4j.LoggerFactory.getLogger(McpRouterAgent.class)
				.info("{}", agent.call("Look up the definition of checkpoint.").text());
		};
	}

	public static void main(String[] args) {
		try (var context = new SpringApplicationBuilder(McpRouterAgent.class).web(WebApplicationType.NONE).run(args)) {
			org.slf4j.LoggerFactory.getLogger(McpRouterAgent.class).info("MCP agent request completed");
		}
	}

}
