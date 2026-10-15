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
import io.github.agentic.ai.api.chat.ChatClientCapabilities;
import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.api.chat.ChatRequest;
import io.github.agentic.ai.api.chat.ChatResponse;
import io.github.agentic.ai.api.chat.FinishReason;
import io.github.agentic.ai.api.chat.Usage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlannerRuntimeTest {
    @Test
    void directModeAvoidsModelCallsAndDecompositionValidatesTheReturnedPlan() {
        var client = mock(ChatClient.class);
        var direct = PlannerExample.plan(client, io.github.agentic.ai.graph.agent.planner.ArgiPlanner.Mode.DIRECT);
        assertEquals(1, direct.steps().size());
        org.mockito.Mockito.verifyNoInteractions(client);
        var plan = new io.github.agentic.ai.graph.agent.planner.PlanSpec(1, 1,
            "Research checkpoint recovery and review the findings.",
            List.of(PlanValidationScenarios.step("research", List.of(), "agent:research")));
        String json = new io.github.agentic.ai.graph.agent.planner.PlanCodec().encode(plan);
        when(client.callBlocking(any())).thenReturn(new ChatResponse("id", "fixture",
            List.of(ChatMessage.assistant(json)), List.of(), FinishReason.STOP, Usage.unknown(), Map.of()));
        try (var factory = mockStatic(SpringAiExample.class)) {
            factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            PlannerExample.main(new String[0]);
            PlannerExample.main(new String[]{"DIRECT"});
        }
        verify(client).callBlocking(any());
    }
}
