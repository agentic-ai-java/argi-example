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

import io.github.agentic.ai.graph.store.stores.MemoryStore;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PreferenceRepositoryTest {
    @Test
    void upsertsWithinOneNamespaceAndPagesOnlyActivePreferences() {
        var repository = new PreferenceRepository(new MemoryStore());
        repository.save("ada", "a", Map.of("active", true, "value", "old"));
        repository.save("ada", "a", Map.of("active", true, "value", "new"));
        repository.save("ada", "b", Map.of("active", true));
        repository.save("ada", "c", Map.of("active", false));
        repository.save("grace", "a", Map.of("active", true, "value", "separate"));
        assertEquals("new", repository.find("ada", "a").orElseThrow().getValue().get("value"));
        assertEquals("separate", repository.find("grace", "a").orElseThrow().getValue().get("value"));
        var first = repository.page("ada", 0, 1);
        assertEquals(2, first.getTotalCount());
        assertEquals("a", first.getItems().get(0).getKey());
        assertTrue(first.hasMore());
        assertEquals("b", repository.page("ada", 1, 1).getItems().get(0).getKey());
        assertTrue(repository.page("ada", 2, 1).getItems().isEmpty());
        assertTrue(repository.delete("ada", "a"));
        assertFalse(repository.delete("ada", "a"));
        assertTrue(repository.find("grace", "a").isPresent());
    }

    @Test
    void rejectsInvalidScopeAndPagination() {
        var repository = new PreferenceRepository(new MemoryStore());
        assertThrows(IllegalArgumentException.class, () -> repository.find("", "a"));
        assertThrows(IllegalArgumentException.class, () -> repository.page("ada", -1, 1));
        assertThrows(IllegalArgumentException.class, () -> repository.page("ada", 0, 0));
    }
}
