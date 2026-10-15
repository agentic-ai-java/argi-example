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
import io.github.agentic.ai.api.chat.ChatRole;
import io.github.agentic.ai.api.chat.ContentPart;
import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.api.tool.ToolResult;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/** Neutral messages include content, metadata and stable tool invocation identifiers. */
public final class MessageFixtures {
    private MessageFixtures() {}

    public static List<ChatMessage> toolConversation() {
        var call = new ToolCall("tracking-42", "track_order", "{\"orderId\":\"ORDER-42\"}");
        return List.of(
            ChatMessage.system("Use the tracking tool for delivery facts."),
            ChatMessage.user("Where is my order?").withMetadata(Map.of("customer", "ada")),
            ChatMessage.assistant("", List.of(call)),
            ToolResult.text("Dispatched").toMessage(call),
            ChatMessage.assistant("Your order has been dispatched."));
    }

    public static ChatMessage imageQuestion() {
        byte[] png = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a7xkAAAAASUVORK5CYII=");
        return ChatMessage.builder(ChatRole.USER)
            .content(List.of(ContentPart.text("Describe this one-pixel image."),
                ContentPart.binary(ContentPart.Type.IMAGE, "image/png", png)))
            .metadata(Map.of("source", "embedded-fixture")).build();
    }
}
