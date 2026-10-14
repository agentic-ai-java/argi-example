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

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import java.util.*;

import org.springframework.ai.chat.memory.*;
import org.springframework.ai.chat.messages.*;

/** Each Maven profile supplies exactly one real repository starter. */
@SpringBootApplication
public class MemoryBackends {
    @Bean ApplicationRunner conversation(ChatMemoryRepository repository) {
        return args -> {
            var memory = MessageWindowChatMemory.builder().chatMemoryRepository(repository).maxMessages(6).build();
            String conversation = "argi-example-" + UUID.randomUUID();
            try {
                memory.add(conversation, new UserMessage("I prefer short technical answers."));
                memory.add(conversation, new AssistantMessage("I will keep answers concise."));
                org.slf4j.LoggerFactory.getLogger(MemoryBackends.class).info("{}: {}", repository.getClass().getSimpleName(), memory.get(conversation));
            } finally { memory.clear(conversation); }
        };
    }
    public static void main(String[] args) {
        try (var context = new SpringApplicationBuilder(MemoryBackends.class).web(WebApplicationType.NONE).run(args)) {
            org.slf4j.LoggerFactory.getLogger(MemoryBackends.class).info("Conversation persistence example completed");
        }
    }
}
