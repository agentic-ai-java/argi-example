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

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class KnowledgeSearchTest {
    @Test
    void carriesRetrievalPolicyAndReturnsPlainData() {
        var store = mock(VectorStore.class);
        when(store.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(KnowledgeCorpus.documents().get(0)));
        var search = new KnowledgeSearch(store);
        search.index();
        var result = search.search("resume", 2, "topic == 'checkpoint'");
        var request = ArgumentCaptor.forClass(SearchRequest.class);
        verify(store).similaritySearch(request.capture());
        assertEquals(2, request.getValue().getTopK());
        assertEquals("resume", request.getValue().getQuery());
        assertNotNull(request.getValue().getFilterExpression());
        assertEquals("argi-checkpoint-guide", result.get(0).get("id"));
        search.deleteExampleDocuments();
        verify(store).delete(KnowledgeCorpus.ids());
        assertThrows(IllegalArgumentException.class, () -> search.search("", 2, null));
        assertThrows(IllegalArgumentException.class, () -> search.search("resume", 0, null));
    }
}
