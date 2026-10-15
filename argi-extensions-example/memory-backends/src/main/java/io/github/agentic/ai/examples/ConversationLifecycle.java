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
import java.util.UUID;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

/** Runs the same conversation lifecycle against the profile-selected repository. */
public final class ConversationLifecycle {
    private ConversationLifecycle() {}

    public record Report(List<String> restoredWindow, List<String> otherConversation,
                         boolean cleared, boolean otherConversationRetained) {}

    public static Report exercise(ChatMemoryRepository repository) {
        String first = "argi-window-" + UUID.randomUUID();
        String second = "argi-isolation-" + UUID.randomUUID();
        var memory = MessageWindowChatMemory.builder().chatMemoryRepository(repository).maxMessages(4).build();
        try {
            memory.add(second, new UserMessage("This belongs to another conversation."));
            for (int turn = 1; turn <= 3; turn++) {
                memory.add(first, new UserMessage("Question " + turn));
                memory.add(first, new AssistantMessage("Answer " + turn));
            }
            // A new facade reads the same stored window; no local list is shared.
            var restored = MessageWindowChatMemory.builder().chatMemoryRepository(repository).maxMessages(4).build();
            var window = texts(restored.get(first));
            var independent = texts(restored.get(second));
            restored.clear(first);
            return new Report(window, independent, restored.get(first).isEmpty(),
                texts(restored.get(second)).equals(independent) && !independent.isEmpty());
        } finally {
            memory.clear(first);
            memory.clear(second);
        }
    }

    private static List<String> texts(List<Message> messages) {
        return messages.stream().map(Message::getText).toList();
    }
}
