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
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemoryBackendsTest {
    @Test
    void runnerCleansOnlyItsOwnConversationsAndClosesTheContext() throws Exception {
        var repository = new InMemoryChatMemoryRepository();
        new MemoryBackends().conversation(repository).run(new DefaultApplicationArguments());
        assertTrue(repository.findConversationIds().isEmpty());
        var context = mock(ConfigurableApplicationContext.class);
        try (var construction = mockConstruction(SpringApplicationBuilder.class, (builder, ignored) -> {
            when(builder.web(WebApplicationType.NONE)).thenReturn(builder);
            when(builder.run(any(String[].class))).thenReturn(context);
        })) {
            MemoryBackends.main(new String[0]);
            verify(construction.constructed().get(0)).web(WebApplicationType.NONE);
            verify(context).close();
        }
    }
}
