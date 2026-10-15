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

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JdbcCheckpointTest {
    @TempDir Path directory;
    @Test void restoresFileBackedCheckpointWithANewRuntime() throws Exception {
        String url = "jdbc:h2:file:" + directory.resolve("booking");
        assertEquals("confirmed", JdbcCheckpoint.run(url, true));
        assertEquals("cancelled", JdbcCheckpoint.run("jdbc:h2:file:" + directory.resolve("rejected"), false));
        JdbcCheckpoint.main(new String[0]);
    }
}
