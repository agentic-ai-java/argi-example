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

class SupportReviewTest {
    private static ChatClient client(String text) {
        var client = mock(ChatClient.class);
        when(client.capabilities()).thenReturn(ChatClientCapabilities.basic());
        when(client.callBlocking(any())).thenCallRealMethod();
        when(client.call(any())).thenReturn(CompletableFuture.completedFuture(new ChatResponse("1", "fixture",
            List.of(ChatMessage.assistant(text)), List.of(), FinishReason.STOP, Usage.unknown(), Map.of())));
        return client;
    }
    @Test
    void keepsDraftsPausedUntilAnExplicitDecisionAndIsolatesTickets() throws Exception {
        var client = client("Here is the support reply.");
        var review = new SupportReview(SupportWorkflow.workflow(client));
        var approved = review.submit(new SupportTicket("approved", "Request a refund"));
        var rejected = review.submit(new SupportTicket("rejected", "Debug a checkpoint"));
        assertEquals("billing", review.draft(approved).get("category"));
        assertEquals("technical", review.draft(rejected).get("category"));
        assertThrows(IllegalArgumentException.class, () -> review.decide(approved, true, " "));
        assertEquals("ready-to-send", review.decide(approved, true, "reviewer").get("status"));
        assertEquals("rejected", review.decide(rejected, false, "reviewer").get("status"));
        assertThrows(IllegalStateException.class, () -> review.decide(approved, false, "reviewer"));
        assertThrows(IllegalArgumentException.class, () -> new SupportTicket("", "text"));
        assertThrows(IllegalArgumentException.class, () -> new SupportTicket("id", " "));
        try (var factory = mockStatic(SpringAiExample.class)) {
            factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            SupportWorkflow.main(new String[0]);
            SupportReview.main(new String[0]);
        }
    }
}
