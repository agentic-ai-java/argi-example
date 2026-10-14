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


import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.api.tool.*;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.tools.*;
import io.github.agentic.ai.graph.agent.extension.tools.filesystem.FileSystemTools;
import java.nio.file.*;
import java.util.*;

/** Limits all file tools to a dedicated example workspace supplied by the caller. */
public final class BuiltinToolsExample {
    private BuiltinToolsExample() {}
    public static ReactAgent agent(ChatClient client, Path directory) {
        var tools = new ArrayList<Tool>(FileSystemTools.builder().rootDir(directory.toAbsolutePath().toString()).maxFileSizeMb(1).build().tools());
        tools.add(GlobSearchTool.builder(directory.toAbsolutePath().toString()).maxResults(20).build());
        tools.add(GrepSearchTool.builder(directory.toAbsolutePath().toString()).withUseRipgrep(false).build());
        tools.add(WriteTodosTool.builder().build());
        return ReactAgent.builder().name("workspace_assistant").chatClient(client).toolRegistry(new ToolRegistry(tools))
            .instruction("Inspect notes.txt, make a todo list, and write a concise summary to summary.txt.").build();
    }
    public static void main(String[] args) throws Exception {
        Path workspace = Path.of("target", "tool-workspace");
        Files.createDirectories(workspace);
        Files.writeString(workspace.resolve("notes.txt"), "Add JDBC checkpoints. Document how to resume pending approvals.");
        org.slf4j.LoggerFactory.getLogger(BuiltinToolsExample.class).info("{}", agent(SpringAiExample.client(System.getenv()), workspace).call("Organize the project notes.").text());
    }
}
