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

class StructuredOutputTest {
    private static ChatClient client(String text) {
        var client = mock(ChatClient.class);
        when(client.capabilities()).thenReturn(ChatClientCapabilities.basic());
        when(client.callBlocking(any())).thenCallRealMethod();
        when(client.call(any())).thenReturn(CompletableFuture.completedFuture(new ChatResponse("1", "fixture",
            List.of(ChatMessage.assistant(text)), List.of(), FinishReason.STOP, Usage.unknown(), Map.of())));
        return client;
    }
    @Test
    void selectsSchemaOnlyWhenSupportedAndValidatesBothPaths() throws Exception {
        var client = client("{\"category\":\"billing\",\"priority\":2}");
        assertEquals(new StructuredOutput.Ticket("billing", 2), StructuredOutput.classify(client, "Duplicate charge"));
        when(client.capabilities()).thenReturn(new ChatClientCapabilities(false, false, true, false, true, false));
        assertEquals(2, StructuredOutput.classify(client, "Duplicate charge").priority());
        var requests = org.mockito.ArgumentCaptor.forClass(ChatRequest.class);
        org.mockito.Mockito.verify(client, org.mockito.Mockito.times(2)).call(requests.capture());
        assertTrue(requests.getAllValues().get(0).messages().get(0).text().contains(TicketSchema.JSON));
        assertEquals(TicketSchema.format(), requests.getAllValues().get(1).options().responseFormat());
        try (var factory = mockStatic(SpringAiExample.class)) {
            factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            StructuredOutput.main(new String[0]);
            TypedTicketAgent.main(new String[0]);
            TypedTicketAgent.main(new String[]{"Incorrect bill"});
        }
        var invalid = client("{\"category\":\"other\",\"priority\":2}");
        assertThrows(IllegalArgumentException.class, () -> StructuredOutput.classify(invalid, "Ticket"));
    }
}
