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

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DocumentIngestionTest {
    @TempDir
    Path directory;

    @Test
    void preservesJsonSourceAndReadsUtf8Text() throws Exception {
        var json = directory.resolve("articles.json");
        Files.writeString(json, "{\"articles\":[{\"id\":\"guide-42\",\"title\":\"Resume\",\"body\":\"Saved state\"}]}");
        assertEquals("guide-42", DocumentIngestion.load(json).get(0).source());
        var text = directory.resolve("notes.txt");
        Files.writeString(text, "  Checkpoint recovery  ");
        assertEquals("Checkpoint recovery", DocumentIngestion.load(text).get(0).text());
        var unsupported = directory.resolve("notes.csv");
        Files.writeString(unsupported, "title,body");
        assertThrows(IllegalArgumentException.class, () -> DocumentIngestion.load(unsupported));
    }
}
