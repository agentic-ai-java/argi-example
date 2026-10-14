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

import io.github.agentic.ai.graph.*;
import io.github.agentic.ai.graph.node.rag.KnowledgeRetrievalNode;
import io.github.agentic.ai.rag.preretrieval.transformation.HyDeTransformer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.*;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import static io.github.agentic.ai.graph.StateGraph.*;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Expands a query with HyDE, then retrieves source documents through an Extensions graph node. */
public final class RagRetrieval {
    private RagRetrieval() {}

    public static Map<String, Object> retrieve(ChatModel model, VectorStore store, String question) throws Exception {
        var transformer = HyDeTransformer.builder().chatClientBuilder(ChatClient.builder(model)).build();
        String expanded = transformer.transform(new Query(question)).text();
        var node = KnowledgeRetrievalNode.builder().userPrompt(expanded).vectorStore(store).topK(2)
            .similarityThreshold(0.0).outputKey("documents").build();
        var graph = new StateGraph().addNode("retrieve", node_async(state -> {
            var retrieved = node.apply(state);
            // RC2 checkpoints accept neutral state, not Spring AI Document instances.
            @SuppressWarnings("unchecked")
            var documents = (List<Document>) retrieved.get("documents");
            return Map.of("documents", documents.stream().map(document -> Map.of(
                "id", document.getId(), "text", document.getText(), "metadata", document.getMetadata())).toList(),
                "user_prompt", retrieved.get("user_prompt"));
        }))
            .addEdge(START, "retrieve").addEdge("retrieve", END).compile();
        var state = graph.invoke(Map.of("original_question", question)).orElseThrow();
        return state.data();
    }
    public static ChatModel model(Map<String, String> env) {
        return OpenAiChatModel.builder().options(OpenAiChatOptions.builder().apiKey(env.get("ARGI_API_KEY"))
            .model(env.get("ARGI_MODEL")).baseUrl(env.getOrDefault("ARGI_BASE_URL", "https://api.openai.com/v1"))
            .timeout(Duration.ofSeconds(60)).maxRetries(0).build()).build();
    }
    public static VectorStore store(Map<String, String> env) {
        var embedding = OpenAiEmbeddingModel.builder().options(OpenAiEmbeddingOptions.builder()
            .apiKey(env.get("ARGI_API_KEY")).model(env.getOrDefault("ARGI_EMBEDDING_MODEL", "text-embedding-3-small"))
            .baseUrl(env.getOrDefault("ARGI_BASE_URL", "https://api.openai.com/v1")).timeout(Duration.ofSeconds(60)).maxRetries(0).build()).build();
        return SimpleVectorStore.builder(embedding).build();
    }
    public static void main(String[] args) throws Exception {
        var store = store(System.getenv());
        store.add(List.of(new Document("A checkpoint records graph state and the next node for a thread."),
            new Document("Resume a paused graph with its thread ID and the saved checkpoint cursor.")));
        org.slf4j.LoggerFactory.getLogger(RagRetrieval.class).info("{}", retrieve(model(System.getenv()), store, "How can I resume a workflow?"));
    }
}
