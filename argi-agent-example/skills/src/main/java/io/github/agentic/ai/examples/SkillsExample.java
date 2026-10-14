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

import io.github.agentic.ai.api.chat.*;
import io.github.agentic.ai.api.tool.*;
import io.github.agentic.ai.graph.agent.ReactAgent;
import java.util.*;

import io.github.agentic.ai.graph.skills.ArgiSkillChatClient;
import io.github.agentic.ai.graph.skills.registry.classpath.ClasspathSkillRegistry;
import io.github.agentic.ai.graph.agent.hook.skills.ReadSkillTool;

/** Uses the same registry for skill summaries and full skill reads. */
public final class SkillsExample {
    private SkillsExample() {}
    public static String answer(ChatClient client) throws Exception {
        var registry = ClasspathSkillRegistry.builder().classpathPath("skills").basePath("target/skills-cache").build();
        try {
            var agent = ReactAgent.builder().name("release_writer")
                .chatClient(new ArgiSkillChatClient(client, registry))
                .toolRegistry(new ToolRegistry(List.of(ReadSkillTool.createReadSkillTool(registry, null))))
                .instruction("Read the release-notes skill and follow it when writing.").build();
            return agent.call("Write release notes for a change that adds JDBC checkpoint recovery.").text();
        } finally {
            registry.close();
        }
    }
    public static void main(String[] args) throws Exception {
        org.slf4j.LoggerFactory.getLogger(SkillsExample.class).info("{}", answer(SpringAiExample.client(System.getenv())));
    }
}
