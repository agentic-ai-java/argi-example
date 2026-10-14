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

class MultiAgentTest {
    @Test void parallelReviewAndBoundedRevisionProduceChecklist() throws Exception {
        var client = mock(ChatClient.class);
        when(client.callBlocking(any())).thenCallRealMethod();
        when(client.capabilities()).thenReturn(ChatClientCapabilities.basic());
        when(client.call(any())).thenReturn(CompletableFuture.completedFuture(new ChatResponse("1", "fixture", List.of(ChatMessage.assistant("Verify checkpoint recovery.")), List.of(), FinishReason.STOP, Usage.unknown(), Map.of())));
        var result = MultiAgent.workflow(client).invoke("Review a release.").orElseThrow();
        assertTrue(result.value("checklist").orElseThrow().toString().contains("checkpoint"));
        verify(client, times(4)).call(any());
        try (var factory = mockStatic(SpringAiExample.class)) {
            factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            MultiAgent.main(new String[0]);
        }
    }
}
