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

import io.github.agentic.ai.api.chat.*;
import io.github.agentic.ai.api.tool.*;
import io.github.agentic.ai.graph.agent.ReactAgent;
import java.util.*;

import tools.jackson.databind.json.JsonMapper;

/** Separates a provider's schema request from application-side result validation. */
public final class StructuredOutput {
    private StructuredOutput() {}
    public record Ticket(String category, int priority) {
        public Ticket {
            if (!Set.of("billing", "technical", "general").contains(category) || priority < 1 || priority > 5)
                throw new IllegalArgumentException("Invalid ticket classification");
        }
    }
    public static Ticket classify(ChatClient client, String text) {
        String schema = """
            {"type":"object","properties":{"category":{"type":"string","enum":["billing","technical","general"]},
            "priority":{"type":"integer","minimum":1,"maximum":5}},"required":["category","priority"],"additionalProperties":false}
            """;
        var request = new ChatRequest(List.of(ChatMessage.system("Classify the support ticket."), ChatMessage.user(text)),
            List.of(), GenerationOptions.builder().responseFormat(ResponseFormat.jsonSchema("ticket", schema)).build(), Map.of());
        return JsonMapper.shared().readValue(client.callBlocking(request).text(), Ticket.class);
    }
    public static void main(String[] args) {
        org.slf4j.LoggerFactory.getLogger(StructuredOutput.class).info("{}", classify(SpringAiExample.client(System.getenv()), "I was charged twice."));
    }
}
