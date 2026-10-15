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

import io.github.agentic.ai.api.chat.ChatMessage;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class MessageRoundTripTest {
    @Test
    void preservesToolResultIdentityAndBinaryContent() throws Exception {
        var restored = MessageRoundTrip.restore();
        assertEquals(MessageFixtures.toolConversation(), restored.get("messages"));
        var original = MessageFixtures.imageQuestion();
        var bytes = original.content().get(1).data();
        byte first = bytes[0];
        bytes[0]++;
        assertEquals(first, original.content().get(1).data()[0]);
        @SuppressWarnings("unchecked")
        var attachments = (List<ChatMessage>) restored.get("attachments");
        assertEquals(original.content().get(1), attachments.get(0).content().get(1));
        assertNotEquals(original.text(), attachments.get(0).text());
        assertEquals("tracking-42", MessageFixtures.toolConversation().get(3).toolCallId());
    }
}
