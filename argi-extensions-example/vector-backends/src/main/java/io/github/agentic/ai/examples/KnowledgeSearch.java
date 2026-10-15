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
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

/** Keeps retrieval policy and provider documents outside an application's graph state. */
public final class KnowledgeSearch {
    private final VectorStore store;

    public KnowledgeSearch(VectorStore store) {
        this.store = store;
    }

    public void index() {
        store.add(KnowledgeCorpus.documents());
    }

    public List<Map<String, Object>> search(String question, int topK, String filter) {
        if (question == null || question.isBlank() || topK < 1) {
            throw new IllegalArgumentException("A question and positive topK are required");
        }
        var builder = SearchRequest.builder().query(question).topK(topK).similarityThreshold(0.0);
        if (filter != null && !filter.isBlank()) {
            builder.filterExpression(filter);
        }
        return store.similaritySearch(builder.build()).stream()
            .map(document -> Map.<String, Object>of("id", document.getId(),
                "text", document.getText(), "metadata", document.getMetadata())).toList();
    }

    /** Only call when the selected backend supports deletion by ID. */
    public void deleteExampleDocuments() {
        store.delete(KnowledgeCorpus.ids());
    }
}
