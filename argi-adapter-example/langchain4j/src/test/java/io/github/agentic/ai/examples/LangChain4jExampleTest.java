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

class LangChain4jExampleTest {
    @Test void validatesConfigurationAndAdaptsNativeModel() {
        assertThrows(IllegalArgumentException.class, () -> LangChain4jExample.client(Map.of()));
        assertThrows(IllegalArgumentException.class, () -> LangChain4jExample.client(Map.of("ARGI_API_KEY", " ")));
        assertThrows(IllegalArgumentException.class, () -> LangChain4jExample.client(Map.of("ARGI_API_KEY", "test")));
        var client = LangChain4jExample.client(Map.of("ARGI_API_KEY", "test", "ARGI_MODEL", "test-model", "ARGI_BASE_URL", "http://127.0.0.1:1/v1"));
        assertTrue(client.capabilities().toolCalling());
        assertTrue(client.capabilities().async());
    }
    @Test void passesQuestionThroughNeutralContract() {
        var client = mock(ChatClient.class);
        when(client.callBlocking(any())).thenReturn(new ChatResponse("1", "fixture", List.of(ChatMessage.assistant("Saved state.")), List.of(), FinishReason.STOP, Usage.unknown(), Map.of()));
        assertEquals("Saved state.", LangChain4jExample.ask(client, "What is a checkpoint?"));
        verify(client).callBlocking(argThat(request -> request.messages().get(0).text().equals("What is a checkpoint?")));
        // Main uses exactly the same application path, without a paid model request.
        try (var factory = mockStatic(LangChain4jExample.class, CALLS_REAL_METHODS)) {
            factory.when(() -> LangChain4jExample.client(System.getenv())).thenReturn(client);
            LangChain4jExample.main(new String[0]);
        }
    }
}
