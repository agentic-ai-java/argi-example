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

import io.github.agentic.ai.agent.studio.loader.ContextScanningAgentLoader;
import io.github.agentic.ai.api.chat.ChatClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudioChatbotTest {
    @Test void exposesAgentUnderItsPublicStudioName() {
        var client = mock(ChatClient.class);
        try (var factory = mockStatic(SpringAiExample.class);
             var boot = mockStatic(SpringApplication.class);
             var context = new AnnotationConfigApplicationContext()) {
            factory.when(() -> SpringAiExample.client(System.getenv())).thenReturn(client);
            var app = new StudioChatbot();
            assertSame(client, app.chatClient());
            context.registerBean("supportAgent", io.github.agentic.ai.graph.agent.ReactAgent.class, () -> app.supportAgent(client));
            context.refresh();
            var loader = new ContextScanningAgentLoader(context);
            assertEquals(java.util.List.of("studio_support"), loader.listAgents());
            assertEquals("studio_support", loader.loadAgent("studio_support").name());
            StudioChatbot.main(new String[0]);
            boot.verify(() -> SpringApplication.run(eq(StudioChatbot.class), any(String[].class)));
        }
    }
}
