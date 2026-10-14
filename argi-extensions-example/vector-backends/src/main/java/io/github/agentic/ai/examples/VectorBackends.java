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

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import java.util.*;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.*;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.*;
import java.time.Duration;

/** Uses a real embedding provider and the selected Extensions VectorStore starter. */
@SpringBootApplication
public class VectorBackends {
    @Bean EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder().options(OpenAiEmbeddingOptions.builder()
            .apiKey(System.getenv("ARGI_API_KEY")).model(System.getenv().getOrDefault("ARGI_EMBEDDING_MODEL", "text-embedding-3-small"))
            .baseUrl(System.getenv().getOrDefault("ARGI_BASE_URL", "https://api.openai.com/v1"))
            .timeout(Duration.ofSeconds(60)).maxRetries(0).build()).build();
    }
    @Bean ApplicationRunner indexAndSearch(VectorStore store) {
        return args -> {
            // A stable ID makes repeat runs update the dedicated example document.
            store.add(List.of(new Document("argi-checkpoint-guide", "Checkpoints preserve workflow state so a thread can resume.",
                Map.of("source", "argi-examples", "topic", "checkpoint"))));
            var request = SearchRequest.builder().query("How do I resume a workflow?").topK(3).similarityThreshold(0.0).build();
            var log = org.slf4j.LoggerFactory.getLogger(VectorBackends.class);
            log.info("{}: {}", store.getClass().getSimpleName(), store.similaritySearch(request));
            // Metadata filters are demonstrated only when explicitly requested; not every backend supports them.
            if (args.containsOption("filter")) log.info("Filtered: {}", store.similaritySearch(SearchRequest.builder()
                .query("How do I resume a workflow?").topK(3).filterExpression("topic == 'checkpoint'").build()));
        };
    }
    public static void main(String[] args) {
        try (var context = new SpringApplicationBuilder(VectorBackends.class).web(WebApplicationType.NONE).run(args)) {
            org.slf4j.LoggerFactory.getLogger(VectorBackends.class).info("Indexing and retrieval completed");
        }
    }
}
