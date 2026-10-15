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

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ConversationLifecycleTest {
    @Test
    void evictsOldTurnsRestoresWindowAndCleansOnlyItsOwnConversations() {
        var repository = new InMemoryChatMemoryRepository();
        var result = ConversationLifecycle.exercise(repository);
        assertEquals(List.of("Question 2", "Answer 2", "Question 3", "Answer 3"), result.restoredWindow());
        assertEquals(List.of("This belongs to another conversation."), result.otherConversation());
        assertTrue(result.cleared());
        assertTrue(result.otherConversationRetained());
        assertTrue(repository.findConversationIds().isEmpty());
    }
}
