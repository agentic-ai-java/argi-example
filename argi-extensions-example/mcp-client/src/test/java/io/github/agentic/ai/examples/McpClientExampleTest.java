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

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class McpClientExampleTest {
    @Test void initializesAndReadsEveryPage() {
        var client = mock(McpSyncClient.class);
        var tool = McpSchema.Tool.builder().name("lookup").description("Read a record").inputSchema(new McpSchema.JsonSchema("object", java.util.Map.of(), List.of(), false, null, null)).build();
        when(client.listTools((String) null)).thenReturn(new McpSchema.ListToolsResult(List.of(tool), "page-2"));
        when(client.listTools("page-2")).thenReturn(new McpSchema.ListToolsResult(List.of(), null));
        assertEquals(List.of(tool), McpClientExample.tools(client));
        verify(client).initialize();
        verify(client).listTools("page-2");
        try (var unused = McpClientExample.connect("http://127.0.0.1:1", "/mcp")) { assertNotNull(unused); }
        try (var factory = mockStatic(McpClientExample.class, CALLS_REAL_METHODS)) {
            factory.when(() -> McpClientExample.connect("http://127.0.0.1:8092", "/mcp")).thenReturn(client);
            McpClientExample.main(new String[0]);
        }
        verify(client).close();
    }
}
