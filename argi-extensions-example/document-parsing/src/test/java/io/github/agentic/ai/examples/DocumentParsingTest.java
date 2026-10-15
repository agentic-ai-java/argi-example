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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentParsingTest {
    @Test void retainsSourceMetadataAndSelectsFields() {
        var docs = DocumentParsing.records("{\"articles\":[{\"id\":\"a\",\"title\":\"Recovery\",\"body\":\"Saved state\",\"secret\":\"excluded\"}]}");
        assertEquals("a", docs.get(0).getMetadata().get("source"));
        assertTrue(docs.get(0).getText().contains("Recovery"));
        assertFalse(docs.get(0).getText().contains("excluded"));
        assertEquals("UTF-8: café", DocumentParsing.text("UTF-8: café").get(0).getText());
        assertThrows(IllegalArgumentException.class, () -> DocumentParsing.records("{}"));
        assertThrows(IllegalArgumentException.class, () -> DocumentParsing.text(" "));
        DocumentParsing.main(new String[0]);
    }
}
