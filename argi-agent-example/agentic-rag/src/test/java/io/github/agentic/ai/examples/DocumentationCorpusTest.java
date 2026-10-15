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

import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.api.tool.ToolContext;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DocumentationCorpusTest {
    @Test
    void selectsSourcesDeterministicallyAndDoesNotInventEvidence() {
        var corpus = DocumentationCorpus.sample();
        assertEquals(List.of("approval", "checkpoint"),
            corpus.search("CHECKPOINT and approval").stream().map(DocumentationCorpus.Page::id).toList());
        assertEquals("No matching source.", corpus.evidence("weather"));
        assertTrue(corpus.search(null).isEmpty());
        assertTrue(corpus.search(" ").isEmpty());
        assertTrue(corpus.evidence("recover").contains("[checkpoint]"));
    }

    @Test
    void returnsSourceLabelsAndRejectsMissingQueries() {
        var tool = new DocumentationSearchTool(DocumentationCorpus.sample());
        var call = new ToolCall("search-1", tool.definition().name(), "{\"query\":\"tool\"}");
        var result = tool.execute(call, ToolContext.empty()).toCompletableFuture().join();
        assertFalse(result.error());
        assertTrue(result.toMessage(call).text().contains("[tool]"));
        assertTrue(tool.execute(new ToolCall("search-2", tool.definition().name(), "{}"), ToolContext.empty())
            .toCompletableFuture().join().error());
    }
}
