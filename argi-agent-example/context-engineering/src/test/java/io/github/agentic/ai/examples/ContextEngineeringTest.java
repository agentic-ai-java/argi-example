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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.agent.hook.modelcalllimit.ModelCallLimitExceededException;
class ContextEngineeringTest {
    @Test void historyIsScopedAndBudgetStopsTheThirdModelTurn() throws Exception {
        var client = mock(ChatClient.class);
        when(client.callBlocking(any())).thenCallRealMethod();
        when(client.capabilities()).thenReturn(ChatClientCapabilities.basic());
        when(client.call(any())).thenReturn(CompletableFuture.completedFuture(new ChatResponse("1", "fixture", List.of(ChatMessage.assistant("ORDER-42")), List.of(), FinishReason.STOP, Usage.unknown(), Map.of())));
        var agent = ContextEngineering.agent(client);
        var thread = RunnableConfig.builder().threadId("one").build();
        agent.call("Remember ORDER-42", thread);
        agent.call("Recall my order", thread);
        var requests = org.mockito.ArgumentCaptor.forClass(ChatRequest.class);
        verify(client, times(2)).call(requests.capture());
        assertTrue(requests.getAllValues().get(1).messages().stream().anyMatch(message -> message.text().contains("Remember ORDER-42")));
        assertThrows(ModelCallLimitExceededException.class, () -> agent.call("Third turn", thread));
        agent.call("New session", RunnableConfig.builder().threadId("two").build());
        verify(client, times(3)).call(any());
        assertEquals(2, ContextEngineering.conversation(client).size());
        try (var factory = mockStatic(SpringAiExample.class)) {
            factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            ContextEngineering.main(new String[0]);
        }
    }
}
