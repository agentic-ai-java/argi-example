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

import io.github.agentic.ai.api.tool.ToolContext;
class ReactToolsTest {
    @Test void runtimeExecutesToolAndReturnsItsResult() throws Exception {
        var client = mock(ChatClient.class);
        when(client.callBlocking(any())).thenCallRealMethod();
        when(client.capabilities()).thenReturn(new ChatClientCapabilities(false, true, false, false, true, false));
        var call = new ToolCall("tracking-1", "track_order", "{\"orderId\":\"ORDER-42\"}");
        when(client.call(any())).thenAnswer(invocation -> {
            ChatRequest request = invocation.getArgument(0);
            var result = request.messages().stream().filter(message -> message.role() == ChatRole.TOOL).findFirst();
            var message = result.isPresent() ? ChatMessage.assistant(result.get().text()) : ChatMessage.assistant("", List.of(call));
            return CompletableFuture.completedFuture(new ChatResponse("1", "fixture", List.of(message), message.toolCalls(), result.isPresent() ? FinishReason.STOP : FinishReason.TOOL_CALLS, Usage.unknown(), Map.of()));
        });
        assertTrue(ReactTools.agent(client).call("Track ORDER-42").text().contains("Friday"));
        verify(client, times(2)).call(any());
        assertTrue(ReactTools.trackingTool().execute(new ToolCall("x", "track_order", "{\"orderId\":\"missing\"}"), mock(ToolContext.class)).toCompletableFuture().join().error());
        try (var factory = mockStatic(SpringAiExample.class)) {
            factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            ReactTools.main(new String[0]);
        }
    }
}
