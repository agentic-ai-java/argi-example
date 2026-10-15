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

import io.github.agentic.ai.document.JsonDocumentParser;
import io.github.agentic.ai.document.TextDocumentParser;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.ai.document.Document;

/** Parses JSON records and UTF-8 text before indexing documents. */
public final class DocumentParsing {
    private DocumentParsing() {}

    public static List<Document> records(String json) {
        var parser = new JsonDocumentParser(item -> java.util.Map.of("source", item.get("id")), "title", "body");
        return parser.get("/articles", new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }
    public static List<Document> text(String text) {
        return new TextDocumentParser().parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }
    public static void main(String[] args) {
        var documents = records("{\"articles\":[{\"id\":\"guide-1\",\"title\":\"Checkpoints\",\"body\":\"Resume a saved thread.\"}]}");
        org.slf4j.LoggerFactory.getLogger(DocumentParsing.class).info("{} / {}", documents, text("ARGI keeps workflow state."));
    }
}
