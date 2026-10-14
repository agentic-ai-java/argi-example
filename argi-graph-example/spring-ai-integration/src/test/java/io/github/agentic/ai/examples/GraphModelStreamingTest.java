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
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.adapter.JdkFlowAdapter;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GraphModelStreamingTest {
    @Test void commitsTheFinalNeutralResponseAfterStreaming() throws Exception {
        var client = mock(ChatClient.class);
        when(client.capabilities()).thenReturn(new ChatClientCapabilities(true, false, false, false, true, true));
        var response = new ChatResponse("id", "fixture", List.of(ChatMessage.assistant("Saved state")), List.of(), FinishReason.STOP, Usage.unknown(), Map.of());
        when(client.stream(any())).thenAnswer(call -> JdkFlowAdapter.publisherToFlowPublisher(Flux.just(
            ChatEvent.textDelta("Saved "), ChatEvent.textDelta("state"), ChatEvent.response(response))));
        var events = GraphModelStreaming.workflow(client).stream(Map.of("question", "Explain")).collectList().block();
        assertTrue(events.stream().anyMatch(event -> event.state().value("answer").isPresent()));
        try (var model = mockStatic(SpringAiExample.class)) {
            model.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            GraphModelStreaming.main(new String[0]);
        }
    }
}
