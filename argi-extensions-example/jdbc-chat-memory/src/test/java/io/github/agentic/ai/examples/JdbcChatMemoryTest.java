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
import org.springframework.ai.chat.messages.UserMessage;
import static org.junit.jupiter.api.Assertions.*;
class JdbcChatMemoryTest {
    @Test void persistsBoundsAndIsolatesConversations() {
        String url = "jdbc:h2:mem:memory-test;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";
        assertEquals(2, JdbcChatMemory.run(url).size());
        var memory = JdbcChatMemory.memory(url);
        assertTrue(memory.get("other").isEmpty());
        for (int i = 0; i < 6; i++) memory.add("window", new UserMessage("Turn " + i));
        assertEquals(4, memory.get("window").size());
        memory.clear("customer-42");
        assertTrue(memory.get("customer-42").isEmpty());
        JdbcChatMemory.main(new String[0]);
    }
}
