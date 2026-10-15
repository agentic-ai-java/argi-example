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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;

/** Reads caller-selected JSON or text and projects parsed documents into neutral records. */
public final class DocumentIngestion {
    private DocumentIngestion() {}

    public record SourceDocument(String source, String text, Map<String, Object> metadata) {
        public SourceDocument {
            metadata = Map.copyOf(metadata);
        }
    }

    public static List<SourceDocument> load(Path path) throws IOException {
        String content = Files.readString(path);
        List<Document> parsed;
        if (path.toString().endsWith(".json")) {
            parsed = DocumentParsing.records(content);
        } else if (path.toString().endsWith(".txt")) {
            parsed = DocumentParsing.text(content);
        } else {
            throw new IllegalArgumentException("Use a .json or .txt input file");
        }
        var result = new ArrayList<SourceDocument>();
        for (var document : parsed) {
            if (document.getText() == null || document.getText().isBlank()) {
                continue;
            }
            String source = document.getMetadata().getOrDefault("source", path.getFileName().toString()).toString();
            result.add(new SourceDocument(source, document.getText().strip(), document.getMetadata()));
        }
        return List.copyOf(result);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Provide one JSON or text document path");
        }
        for (var document : load(Path.of(args[0]))) {
            LoggerFactory.getLogger(DocumentIngestion.class).info("source={} metadata={} text={}",
                document.source(), document.metadata(), document.text());
        }
    }
}
