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
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.openai.*;
import redis.clients.jedis.Jedis;
import java.util.List;
import java.util.Map;
import java.time.Duration;

/** Writes and searches a dedicated Tair Vector index using real embeddings. */
public final class TairVectorStoreExample {
    private TairVectorStoreExample() {}
    public static TairVectorStore store(TairVectorApi api, EmbeddingModel embedding, int dimensions) {
        var options = new TairVectorStoreOptions();
        options.setIndexName("argi_example_knowledge");
        options.setDimensions(dimensions);
        var store = TairVectorStore.builder(api, embedding).options(options).initializeSchema(true).build();
        store.afterPropertiesSet();
        return store;
    }
    public static List<Document> search(TairVectorStore store) {
        store.add(List.of(new Document("checkpoint-guide", "A checkpoint saves the workflow state for later recovery.", Map.of("source", "example"))));
        return store.similaritySearch(SearchRequest.builder().query("Recover a paused workflow").topK(3).similarityThreshold(0.0).build());
    }
    public static EmbeddingModel embedding(Map<String, String> env) {
        return OpenAiEmbeddingModel.builder().options(OpenAiEmbeddingOptions.builder()
            .apiKey(env.get("ARGI_API_KEY")).model(env.getOrDefault("ARGI_EMBEDDING_MODEL", "text-embedding-3-small"))
            .baseUrl(env.getOrDefault("ARGI_BASE_URL", "https://api.openai.com/v1"))
            .timeout(Duration.ofSeconds(60)).maxRetries(0).build()).build();
    }
    public static void main(String[] args) {
        try (var jedis = new Jedis(java.net.URI.create(System.getenv().getOrDefault("TAIR_URI", "redis://127.0.0.1:6379")))) {
            var store = store(new TairVectorApi(jedis), embedding(System.getenv()),
                Integer.parseInt(System.getenv().getOrDefault("ARGI_EMBEDDING_DIMENSIONS", "1536")));
            org.slf4j.LoggerFactory.getLogger(TairVectorStoreExample.class).info("{}", search(store));
        }
    }
}
