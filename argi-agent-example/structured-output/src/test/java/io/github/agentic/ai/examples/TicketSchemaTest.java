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

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class TicketSchemaTest {
    @Test
    void acceptsBoundaryPriorities() {
        assertEquals(new StructuredOutput.Ticket("billing", 1),
            TicketSchema.parse("{\"category\":\"billing\",\"priority\":1}"));
        assertEquals(new StructuredOutput.Ticket("technical", 5),
            TicketSchema.parse("{\"category\":\"technical\",\"priority\":5}"));
    }

    @Test
    void rejectsExtraMissingAndWronglyTypedFields() {
        for (String json : List.of("null", "[]", "{}", "{\"category\":null,\"priority\":2}",
                "{\"category\":\"other\",\"priority\":2}", "{\"category\":\"general\",\"priority\":0}",
                "{\"category\":\"general\",\"priority\":2.1}", "{\"category\":\"general\",\"priority\":\"2\"}",
                "{\"category\":\"general\",\"priority\":2147483649}",
                "{\"category\":\"general\",\"priority\":18446744073709551617}",
                "{\"category\":\"general\",\"priority\":2,\"extra\":1}")) {
            assertThrows(IllegalArgumentException.class, () -> TicketSchema.parse(json), json);
        }
    }
}
