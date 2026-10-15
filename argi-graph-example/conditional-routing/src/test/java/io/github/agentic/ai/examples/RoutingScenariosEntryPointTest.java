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

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks the documented CLI outcome in addition to the helper-level tests. */
class RoutingScenariosEntryPointTest {
    @Test
    void reportsTheDocumentedOutcome() throws Exception {
        var logger = (Logger) LoggerFactory.getLogger(RoutingScenarios.class);
        var events = new ListAppender<ILoggingEvent>();
        events.start();
        logger.addAppender(events);
        try {
            RoutingScenarios.main(new String[]{});
            String output = events.list.stream().map(ILoggingEvent::getFormattedMessage)
                .reduce("", (left, right) -> left + "\n" + right);
            assertTrue(output.contains("severity=3 queue=business-hours"), output);
            assertTrue(output.contains("severity=4 queue=on-call"), output);
        } finally {
            logger.detachAppender(events);
            events.stop();
        }
    }
}
