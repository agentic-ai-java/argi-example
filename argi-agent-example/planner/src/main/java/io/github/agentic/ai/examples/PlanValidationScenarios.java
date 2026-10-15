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

import io.github.agentic.ai.graph.agent.planner.PlanSpec;
import io.github.agentic.ai.graph.agent.planner.PlanStep;
import io.github.agentic.ai.graph.agent.planner.PlanValidationException;
import java.util.List;
import org.slf4j.LoggerFactory;

/** Validates plans locally; no capability is executed by validation. */
public final class PlanValidationScenarios {
    private PlanValidationScenarios() {}

    public static PlanStep step(String id, List<String> dependencies, String target) {
        return new PlanStep(id, "Complete " + id, dependencies, target,
            PlanningCatalog.CONTRACT, PlanningCatalog.CONTRACT, "Produce reviewed findings",
            PlanStep.Status.PENDING, null, null);
    }

    public static PlanSpec plan(List<PlanStep> steps) {
        return new PlanSpec(PlanSpec.CURRENT_SCHEMA_VERSION, 1, "Review checkpoint recovery", steps);
    }

    public static void main(String[] args) {
        var validator = PlanningCatalog.validator();
        validator.validateGenerated(plan(List.of(
            step("research", List.of(), "agent:research"),
            step("review", List.of("research"), "agent:review"))));
        var invalidPlans = List.of(
            plan(List.of(step("unknown", List.of(), "agent:unregistered"))),
            plan(List.of(step("review", List.of("missing"), "agent:review"))),
            plan(List.of(step("a", List.of("b"), "agent:research"), step("b", List.of("a"), "agent:review"))),
            plan(List.of(step("same", List.of(), "agent:research"), step("same", List.of(), "agent:review"))));
        for (var invalid : invalidPlans) {
            try {
                validator.validateGenerated(invalid);
                throw new IllegalStateException("Invalid plan was accepted");
            } catch (PlanValidationException expected) {
                LoggerFactory.getLogger(PlanValidationScenarios.class).info("Rejected plan: {}", expected.getMessage());
            }
        }
    }
}
