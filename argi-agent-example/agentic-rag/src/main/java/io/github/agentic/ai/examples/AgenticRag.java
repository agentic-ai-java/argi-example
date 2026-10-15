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

import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.api.tool.Tool;
import io.github.agentic.ai.api.tool.ToolRegistry;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.hook.messages.MessagesModelHook;
import java.util.List;

/** A local documentation corpus makes the retrieval strategy explicit and reproducible. */
public final class AgenticRag {
    private AgenticRag() {}
    public static String retrieve(String query) {
        return DocumentationCorpus.sample().evidence(query);
    }

    public static Tool searchTool() {
        return new DocumentationSearchTool(DocumentationCorpus.sample());
    }

    public static MessagesModelHook evidenceHook() {
        return new EvidenceHook(DocumentationCorpus.sample());
    }

    public static ReactAgent agent(ChatClient client, boolean preloadEvidence) {
        var builder = ReactAgent.builder().throwOnModelError(true).name("documentation_assistant").chatClient(client)
            .instruction("Use search_docs when more evidence is needed. Cite source labels. Say when a source is missing.")
            .toolRegistry(new ToolRegistry(List.of(searchTool())));
        if (preloadEvidence) builder.hooks(List.of(evidenceHook()));
        return builder.build();
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(AgenticRag.class).info("{}", agent(SpringAiExample.client(System.getenv()), true)
            .call("How do checkpoint and approval work together?").text());
    }
}
