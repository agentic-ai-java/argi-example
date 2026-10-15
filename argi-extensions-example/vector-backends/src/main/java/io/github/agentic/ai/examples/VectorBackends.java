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

import java.time.Duration;
import java.util.Map;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;


/** Uses a real embedding provider and the selected Extensions VectorStore starter. */
@SpringBootApplication
public class VectorBackends {
    @Bean EmbeddingModel embeddingModel() {
        return embeddingModel(System.getenv());
    }

    static EmbeddingModel embeddingModel(Map<String, String> environment) {
        String key = environment.get("ARGI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Set ARGI_API_KEY before running the embedding example");
        }
        return OpenAiEmbeddingModel.builder().options(OpenAiEmbeddingOptions.builder()
            .apiKey(key).model(environment.getOrDefault("ARGI_EMBEDDING_MODEL", "text-embedding-3-small"))
            .baseUrl(environment.getOrDefault("ARGI_BASE_URL", "https://api.openai.com/v1"))
            .timeout(Duration.ofSeconds(60)).maxRetries(0).build()).build();
    }
    @Bean ApplicationRunner indexAndSearch(VectorStore store) {
        return args -> {
            var search = new KnowledgeSearch(store);
            search.index();
            var log = org.slf4j.LoggerFactory.getLogger(VectorBackends.class);
            try {
                log.info("Unfiltered: {}", search.search("How do I resume a workflow?", 3, null));
                if (args.containsOption("filter")) {
                    log.info("Filtered: {}", search.search("How do I resume a workflow?", 3,
                        "source == 'argi-examples' && topic == 'checkpoint'"));
                }
            } finally {
                // Deletion and filters are opt-in because backend support differs.
                if (args.containsOption("delete-indexed")) {
                    search.deleteExampleDocuments();
                }
            }
        };
    }
    public static void main(String[] args) {
        try (var context = new SpringApplicationBuilder(VectorBackends.class).web(WebApplicationType.NONE).run(args)) {
            org.slf4j.LoggerFactory.getLogger(VectorBackends.class).info("Indexing and retrieval completed");
        }
    }
}
