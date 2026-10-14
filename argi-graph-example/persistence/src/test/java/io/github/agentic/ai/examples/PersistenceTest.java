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
import org.junit.jupiter.api.io.TempDir;
import io.github.agentic.ai.graph.RunnableConfig;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PersistenceTest {
    @TempDir Path directory;
    @Test void resumesWithANewGraphAndKeepsThreadsSeparate() throws Exception {
        var config = RunnableConfig.builder().threadId("one").build();
        Persistence.workflow(directory).invoke(Map.of("invoice", "one"), config);
        var restored = Persistence.workflow(directory);
        assertEquals("pending", restored.getState(config).state().value("status").orElseThrow());
        assertEquals("complete", restored.invoke((Map<String, Object>) null, config.withResume()).orElseThrow().value("status").orElseThrow());
        assertFalse(restored.getStateHistory(config).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> Persistence.main(new String[0]));
        Persistence.main(new String[]{"prepare"});
        Persistence.main(new String[]{"resume"});
    }
}
