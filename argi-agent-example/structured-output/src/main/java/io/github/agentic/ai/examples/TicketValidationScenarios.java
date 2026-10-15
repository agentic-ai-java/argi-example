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
import org.slf4j.LoggerFactory;

/** Shows that valid JSON alone does not make a valid domain result. */
public final class TicketValidationScenarios {
    private TicketValidationScenarios() {}

    public static void main(String[] args) {
        var log = LoggerFactory.getLogger(TicketValidationScenarios.class);
        log.info("Valid ticket: {}", TicketSchema.parse("{\"category\":\"billing\",\"priority\":2}"));
        for (String invalid : List.of(
                "{\"category\":\"other\",\"priority\":2}",
                "{\"category\":\"billing\",\"priority\":9}",
                "{\"category\":\"billing\",\"priority\":2.5}",
                "{\"category\":\"billing\"}",
                "{\"category\":\"billing\",\"priority\":2,\"extra\":true}")) {
            try {
                TicketSchema.parse(invalid);
                throw new IllegalStateException("Invalid ticket was accepted: " + invalid);
            } catch (IllegalArgumentException expected) {
                log.info("Rejected domain output: {}", expected.getMessage());
            }
        }
    }
}
