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

import io.github.agentic.ai.graph.node.code.CodeExecutor;
import io.github.agentic.ai.graph.node.code.entity.CodeExecutionConfig;
import io.github.agentic.ai.graph.node.code.entity.CodeExecutionResult;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DockerExecutionTest {
    @Test void boundsExecutionAndPropagatesFailures() throws Exception {
        var config = DockerExecution.config(Map.of());
        assertEquals(15, config.getTimeout());
        assertEquals(4096, config.getMaxOutputBytes());
        assertTrue(config.isDisableNetwork());
        var executor = mock(CodeExecutor.class);
        when(executor.executeCodeBlocks(anyList(), same(config))).thenReturn(new CodeExecutionResult(0, "42"));
        assertEquals("42", DockerExecution.run(executor, config));
        verify(executor).executeCodeBlocks(argThat(blocks -> blocks.get(0).language().equals("python") && blocks.get(0).code().contains("19, 23")), same(config));
        when(executor.executeCodeBlocks(anyList(), same(config))).thenReturn(new CodeExecutionResult(1, "failure"));
        assertThrows(IllegalStateException.class, () -> DockerExecution.run(executor, config));
        try (var example = mockStatic(DockerExecution.class, CALLS_REAL_METHODS)) {
            example.when(() -> DockerExecution.run(any(CodeExecutor.class), any(CodeExecutionConfig.class))).thenReturn("42");
            DockerExecution.main(new String[0]);
        }
    }
}
