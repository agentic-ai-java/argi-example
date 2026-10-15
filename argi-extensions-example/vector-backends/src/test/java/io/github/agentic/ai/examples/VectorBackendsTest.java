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
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VectorBackendsTest {
    @Test
    void validatesEmbeddingConfigurationWithoutSendingRequests() {
        assertThrows(IllegalArgumentException.class, () -> VectorBackends.embeddingModel(Map.of()));
        assertNotNull(VectorBackends.embeddingModel(Map.of("ARGI_API_KEY", "fixture", "ARGI_BASE_URL", "http://127.0.0.1:1/v1")));
    }

    @Test
    void runnerAppliesOptInFilteringAndCleansUpAfterFailedRetrieval() throws Exception {
        var store = mock(VectorStore.class);
        when(store.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());
        var runner = new VectorBackends().indexAndSearch(store);
        runner.run(new DefaultApplicationArguments());
        runner.run(new DefaultApplicationArguments("--filter", "--delete-indexed"));
        verify(store, times(3)).similaritySearch(any(SearchRequest.class));
        verify(store).delete(KnowledgeCorpus.ids());
        when(store.similaritySearch(any(SearchRequest.class))).thenThrow(new IllegalStateException("Injected retrieval failure"));
        assertThrows(IllegalStateException.class, () -> runner.run(new DefaultApplicationArguments("--delete-indexed")));
        verify(store, times(2)).delete(KnowledgeCorpus.ids());
        var context = mock(ConfigurableApplicationContext.class);
        try (var construction = mockConstruction(SpringApplicationBuilder.class, (builder, ignored) -> {
            when(builder.web(WebApplicationType.NONE)).thenReturn(builder);
            when(builder.run(any(String[].class))).thenReturn(context);
        })) {
            VectorBackends.main(new String[0]);
            verify(context).close();
        }
    }
}
