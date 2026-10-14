/*
 * Copyright 2026 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy at https://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package io.github.agentic.ai.examples;

import io.github.agentic.ai.adapter.springai.SpringAiChatClientAdapter;
import io.github.agentic.ai.api.chat.*;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** The Spring AI model is adapted once; downstream code uses only ARGI contracts. */
public final class SpringAiExample {
    private SpringAiExample() {}

    public static ChatClient client(Map<String, String> environment) {
        String key = required(environment, "ARGI_API_KEY");
        String model = required(environment, "ARGI_MODEL");
        var options = OpenAiChatOptions.builder().apiKey(key).model(model)
            .baseUrl(environment.getOrDefault("ARGI_BASE_URL", "https://api.openai.com/v1"))
            .timeout(Duration.ofSeconds(60)).maxRetries(0).build();
        return new SpringAiChatClientAdapter(OpenAiChatModel.builder().options(options).build());
    }
    private static String required(Map<String, String> environment, String name) {
        String value = environment.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Set " + name + " before running this example");
        }
        return value;
    }
    public static String ask(ChatClient client, String question) {
        return client.callBlocking(ChatRequest.of(List.of(ChatMessage.user(question)))).text();
    }
    public static void main(String[] args) {
        org.slf4j.LoggerFactory.getLogger(SpringAiExample.class).info("{}", ask(client(System.getenv()), "Explain checkpointing in one sentence."));
    }
}
