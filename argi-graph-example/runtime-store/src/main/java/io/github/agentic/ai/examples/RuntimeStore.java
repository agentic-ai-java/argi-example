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
import java.util.*;
import static io.github.agentic.ai.graph.StateGraph.*;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

import io.github.agentic.ai.graph.store.StoreItem;
import io.github.agentic.ai.graph.store.stores.MemoryStore;
import io.github.agentic.ai.graph.action.AsyncNodeActionWithConfig;

/** Reads a user preference shared by different graph threads through the Store contract. */
public final class RuntimeStore {
    private RuntimeStore() {}
    public static void main(String[] args) throws Exception {
        var store = new MemoryStore();
        store.putItem(StoreItem.of(List.of("users", "ada"), "preferences", Map.of("language", "English")));
        var graph = new StateGraph().addNode("personalize", AsyncNodeActionWithConfig.node_async((state, config) -> {
            String user = config.metadata("user").orElseThrow().toString();
            var preference = config.store().getItem(List.of("users", user), "preferences").orElseThrow();
            return Map.of("language", preference.getValue().get("language"));
        })).addEdge(START, "personalize").addEdge("personalize", END).compile();
        for (String thread : List.of("chat-1", "chat-2")) {
            var config = RunnableConfig.builder().threadId(thread).store(store).addMetadata("user", "ada").build();
            org.slf4j.LoggerFactory.getLogger(RuntimeStore.class).info("{}: {}", thread, graph.invoke(Map.of(), config).orElseThrow().data());
        }
    }
}
