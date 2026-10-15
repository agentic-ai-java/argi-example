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

import io.github.agentic.ai.graph.scheduling.ScheduleConfig;
import java.util.Map;

/** Chooses one schedule mode explicitly; all modes use a bounded demonstration runner. */
public final class SchedulePolicies {
    private SchedulePolicies() {}

    public static ScheduleConfig select(String mode) {
        var builder = ScheduleConfig.builder().inputs(Map.of()).initialDelay(0);
        return switch (mode) {
            case "delay" -> builder.fixedDelay(1000).build();
            case "rate" -> builder.fixedRate(1000).build();
            case "cron" -> builder.cronExpression("*/1 * * * * *").build();
            default -> throw new IllegalArgumentException("Use delay, rate, or cron");
        };
    }
}
