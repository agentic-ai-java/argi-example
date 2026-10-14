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

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.*;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.*;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class RagRetrievalTest {
    @Test void expandsTheQueryAndRetainsRetrievedEvidence() throws Exception {
        var model = mock(ChatModel.class);
        when(model.getOptions()).thenReturn(org.springframework.ai.chat.prompt.ChatOptions.builder().build());
        when(model.call(any(org.springframework.ai.chat.prompt.Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage("Saved checkpoints resume threads.")))));
        var store = mock(VectorStore.class);
        when(store.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(new Document("Use the saved thread ID.")));
        var state = RagRetrieval.retrieve(model, store, "How to resume?");
        assertEquals("How to resume?", state.get("original_question"));
        assertTrue(state.get("documents").toString().contains("saved thread"));
        verify(store).similaritySearch(argThat((SearchRequest request) -> request.getQuery().equals("Saved checkpoints resume threads.") && request.getTopK() == 2));
        var env = Map.of("ARGI_API_KEY", "test", "ARGI_MODEL", "test", "ARGI_BASE_URL", "http://127.0.0.1:1/v1");
        assertNotNull(RagRetrieval.model(env));
        assertNotNull(RagRetrieval.store(env));
        try (var factory = mockStatic(RagRetrieval.class, CALLS_REAL_METHODS)) {
            factory.when(() -> RagRetrieval.model(System.getenv())).thenReturn(model);
            factory.when(() -> RagRetrieval.store(System.getenv())).thenReturn(store);
            RagRetrieval.main(new String[0]);
        }
    }
}
