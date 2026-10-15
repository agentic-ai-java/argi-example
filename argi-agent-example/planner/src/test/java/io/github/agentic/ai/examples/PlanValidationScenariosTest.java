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

import io.github.agentic.ai.graph.agent.planner.PlanValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PlanValidationScenariosTest {
    @Test
    void acceptsRegisteredDependencyChain() {
        var plan = PlanValidationScenarios.plan(List.of(
            PlanValidationScenarios.step("research", List.of(), "agent:research"),
            PlanValidationScenarios.step("review", List.of("research"), "agent:review")));
        assertDoesNotThrow(() -> PlanningCatalog.validator().validateGenerated(plan));
    }

    @Test
    void rejectsUnknownTargetsAndCycles() {
        var unknown = PlanValidationScenarios.plan(List.of(
            PlanValidationScenarios.step("x", List.of(), "agent:unknown")));
        var cycle = PlanValidationScenarios.plan(List.of(
            PlanValidationScenarios.step("a", List.of("b"), "agent:research"),
            PlanValidationScenarios.step("b", List.of("a"), "agent:review")));
        assertThrows(PlanValidationException.class, () -> PlanningCatalog.validator().validateGenerated(unknown));
        assertThrows(PlanValidationException.class, () -> PlanningCatalog.validator().validateGenerated(cycle));
    }
}
