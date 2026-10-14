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

import io.github.agentic.ai.vectorstore.tair.*;
import com.aliyun.tair.tairvector.factory.VectorBuilderFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class TairVectorStoreExampleTest {
    @SuppressWarnings("unchecked")
    @Test void indexesEmbeddingsAndMapsSearchResults() {
        var api = mock(TairVectorApi.class);
        when(api.tvsgetindex(anyString())).thenReturn(Map.of("dim", "2"));
        var embedding = mock(EmbeddingModel.class);
        when(embedding.embed(any(Document.class))).thenReturn(new float[]{1, 0});
        when(embedding.embed(anyString())).thenReturn(new float[]{1, 0});
        VectorBuilderFactory.Knn<String> result = mock(VectorBuilderFactory.Knn.class);
        VectorBuilderFactory.KnnItem<String> item = mock(VectorBuilderFactory.KnnItem.class);
        when(item.getId()).thenReturn("checkpoint-guide");
        when(item.getScore()).thenReturn(1.0);
        when(result.getKnnResults()).thenReturn(List.of(item));
        when(api.tvsknnsearch(anyString(), anyLong(), anyString())).thenReturn(result);
        when(api.tvshmget(anyString(), anyString(), any(String[].class))).thenReturn(List.of("checkpoint-guide", "Saved workflow", "{\"source\":\"example\"}"));
        var store = TairVectorStoreExample.store(api, embedding, 2);
        assertEquals("Saved workflow", TairVectorStoreExample.search(store).get(0).getText());
        verify(api).tvshset(eq("argi_example_knowledge"), eq("checkpoint-guide"), eq("[1.0,0.0]"), any(String[].class));
        assertNotNull(TairVectorStoreExample.embedding(Map.of("ARGI_API_KEY", "test")));
        try (var jedis = mockConstruction(redis.clients.jedis.Jedis.class);
             var example = mockStatic(TairVectorStoreExample.class, CALLS_REAL_METHODS)) {
            example.when(() -> TairVectorStoreExample.embedding(System.getenv())).thenReturn(embedding);
            example.when(() -> TairVectorStoreExample.store(any(TairVectorApi.class), same(embedding), eq(1536))).thenReturn(store);
            TairVectorStoreExample.main(new String[0]);
        }
    }
}
