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

import io.github.agentic.ai.api.chat.ResponseFormat;
import java.math.BigInteger;
import tools.jackson.databind.json.JsonMapper;

/** One schema is shared by the model request and strict application validation. */
public final class TicketSchema {
    private TicketSchema() {}

    public static final String JSON = """
        {"type":"object","properties":{"category":{"type":"string","enum":["billing","technical","general"]},
        "priority":{"type":"integer","minimum":1,"maximum":5}},
        "required":["category","priority"],"additionalProperties":false}
        """;

    public static ResponseFormat format() {
        return ResponseFormat.jsonSchema("ticket", JSON);
    }

    public static StructuredOutput.Ticket parse(String output) {
        var node = JsonMapper.shared().readTree(output);
        if (node == null || !node.isObject() || node.size() != 2
                || !node.path("category").isString() || !node.path("priority").isIntegralNumber()) {
            throw new IllegalArgumentException("Expected exactly a category string and an integer priority");
        }
        // Compare before narrowing so an oversized integer cannot wrap into the valid range.
        var priority = new BigInteger(node.path("priority").toString());
        if (priority.compareTo(BigInteger.ONE) < 0 || priority.compareTo(BigInteger.valueOf(5)) > 0) {
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }
        return new StructuredOutput.Ticket(node.path("category").asString(), priority.intValueExact());
    }
}
