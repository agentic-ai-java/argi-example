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

import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.api.chat.ChatRequest;
import io.github.agentic.ai.api.chat.GenerationOptions;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Separates a provider's schema request from application-side result validation. */
public final class StructuredOutput {
    private StructuredOutput() {}
    public record Ticket(String category, int priority) {
        public Ticket {
            if (category == null || !Set.of("billing", "technical", "general").contains(category)
                    || priority < 1 || priority > 5) {
                throw new IllegalArgumentException("Invalid ticket classification");
            }
        }
    }
    public static Ticket classify(ChatClient client, String text) {
        var options = GenerationOptions.builder();
        // Some adapters require native provider configuration for schema enforcement.
        // The text fallback still validates the complete response against the domain contract.
        if (client.capabilities().structuredOutput()) {
            options.responseFormat(TicketSchema.format());
        }
        var request = new ChatRequest(List.of(ChatMessage.system("Classify the support ticket. "
                + "Return only a JSON object, without Markdown, matching this schema: " + TicketSchema.JSON),
            ChatMessage.user(text)), List.of(), options.build(), Map.of());
        return TicketSchema.parse(client.callBlocking(request).text());
    }
    public static void main(String[] args) {
        org.slf4j.LoggerFactory.getLogger(StructuredOutput.class).info("{}", classify(SpringAiExample.client(System.getenv()), "I was charged twice."));
    }
}
