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
import java.util.Map;
import org.slf4j.LoggerFactory;

/** Calls only the read-only lookup tool supplied by the companion registry example. */
public final class CatalogToolCall {
    private CatalogToolCall() {}

    public static McpSchema.CallToolResult lookup(McpSyncClient client, String concept) {
        var available = McpClientExample.tools(client);
        if (available.stream().noneMatch(tool -> tool.name().equals("lookup"))) {
            throw new IllegalStateException("Connect to the example MCP server exposing lookup");
        }
        var result = client.callTool(new McpSchema.CallToolRequest("lookup", Map.of("concept", concept)));
        if (Boolean.TRUE.equals(result.isError())) {
            throw new IllegalStateException("The lookup tool reported an error: " + result.content());
        }
        return result;
    }

    public static void main(String[] args) {
        try (var client = McpClientExample.connect(
                System.getenv().getOrDefault("MCP_BASE_URL", "http://127.0.0.1:8092"),
                System.getenv().getOrDefault("MCP_ENDPOINT", "/mcp"))) {
            LoggerFactory.getLogger(CatalogToolCall.class).info("Lookup result: {}",
                lookup(client, args.length == 0 ? "checkpoint" : args[0]).content());
        }
    }
}
