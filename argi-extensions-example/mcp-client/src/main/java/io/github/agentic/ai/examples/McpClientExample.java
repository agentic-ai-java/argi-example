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

import io.github.agentic.ai.mcp.common.transport.builder.WebFluxStreamableClientTransportBuilder;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

/** Uses the Extensions HTTP transport to initialize MCP and enumerate all tool pages. */
public final class McpClientExample {
    private McpClientExample() {}

    public static McpSyncClient connect(String baseUrl, String endpoint) {
        var transport = WebFluxStreamableClientTransportBuilder.build(WebClient.builder().baseUrl(baseUrl),
            new JacksonMcpJsonMapper(JsonMapper.shared()), endpoint);
        return McpClient.sync(transport).requestTimeout(Duration.ofSeconds(10)).build();
    }
    public static List<McpSchema.Tool> tools(McpSyncClient client) {
        client.initialize();
        var tools = new ArrayList<McpSchema.Tool>();
        var cursors = new HashSet<String>();
        String cursor = null;
        do {
            var page = client.listTools(cursor);
            tools.addAll(page.tools());
            cursor = page.nextCursor();
            if (cursor != null && !cursor.isBlank() && !cursors.add(cursor)) {
                throw new IllegalStateException("MCP server repeated a tool-list cursor: " + cursor);
            }
        } while (cursor != null && !cursor.isBlank());
        return List.copyOf(tools);
    }
    public static void main(String[] args) {
        try (var client = connect(System.getenv().getOrDefault("MCP_BASE_URL", "http://127.0.0.1:8092"),
            System.getenv().getOrDefault("MCP_ENDPOINT", "/mcp"))) {
            org.slf4j.LoggerFactory.getLogger(McpClientExample.class).info("{}", tools(client));
        }
    }
}
