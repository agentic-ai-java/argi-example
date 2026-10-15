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
import org.springframework.ai.document.Document;

/** Stable document IDs and metadata make unfiltered and filtered searches comparable. */
public final class KnowledgeCorpus {
    private KnowledgeCorpus() {}

    public static List<Document> documents() {
        return List.of(
            new Document("argi-checkpoint-guide",
                "Checkpoints preserve workflow state so a thread can resume.",
                Map.of("source", "argi-examples", "topic", "checkpoint")),
            new Document("argi-approval-guide",
                "Human approval resumes a paused workflow using its saved thread and feedback.",
                Map.of("source", "argi-examples", "topic", "approval")),
            new Document("argi-tool-guide",
                "ARGI executes tool bindings while adapters translate model requests.",
                Map.of("source", "argi-examples", "topic", "tools")));
    }

    public static List<String> ids() {
        return documents().stream().map(Document::getId).toList();
    }
}
