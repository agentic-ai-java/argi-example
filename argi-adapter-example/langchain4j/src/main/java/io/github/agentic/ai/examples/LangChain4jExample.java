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

import io.github.agentic.ai.adapter.langchain4j.LangChain4jChatClientAdapter;
import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.api.chat.ChatRequest;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** Demonstrates native model construction and the neutral ARGI invocation boundary. */
public final class LangChain4jExample {

	private LangChain4jExample() {
	}

	public static ChatClient client(Map<String, String> environment) {
		String key = environment.get("ARGI_API_KEY");
		String model = environment.get("ARGI_MODEL");
		if (key == null || key.isBlank() || model == null || model.isBlank()) {
			throw new IllegalArgumentException("Set ARGI_API_KEY and ARGI_MODEL before running this example");
		}
		String baseUrl = environment.getOrDefault("ARGI_BASE_URL", "https://api.openai.com/v1");
		return new LangChain4jChatClientAdapter(
				dev.langchain4j.model.openai.OpenAiChatModel.builder()
					.apiKey(key)
					.modelName(model)
					.baseUrl(baseUrl)
					.timeout(Duration.ofSeconds(60))
					.maxRetries(0)
					.build(),
				dev.langchain4j.model.openai.OpenAiStreamingChatModel.builder()
					.apiKey(key)
					.modelName(model)
					.baseUrl(baseUrl)
					.timeout(Duration.ofSeconds(60))
					.build());
	}

	public static String ask(ChatClient client, String question) {
		return client.callBlocking(ChatRequest.of(List.of(ChatMessage.user(question)))).text();
	}

	public static void main(String[] args) {
		org.slf4j.LoggerFactory.getLogger(LangChain4jExample.class)
			.info("{}", ask(client(System.getenv()), "Explain checkpointing in one sentence."));
	}

}
