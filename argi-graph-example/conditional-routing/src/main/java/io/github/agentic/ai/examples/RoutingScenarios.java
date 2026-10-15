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

import java.util.Map;
import org.slf4j.LoggerFactory;

/** Compares both sides of the severity boundary using the same compiled graph. */
public final class RoutingScenarios {
    private RoutingScenarios() {}

    public static void main(String[] args) throws Exception {
        var graph = ConditionalRouting.workflow();
        for (int severity : new int[] {1, 3, 4, 5}) {
            var result = graph.invoke(Map.of("severity", severity)).orElseThrow();
            LoggerFactory.getLogger(RoutingScenarios.class).info("severity={} queue={}",
                severity, result.value("queue").orElseThrow());
        }
    }
}
