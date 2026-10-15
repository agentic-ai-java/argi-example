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

import io.github.agentic.ai.graph.OverAllState;
import io.github.agentic.ai.graph.serializer.plain_text.jackson.ArgiJacksonStateSerializer;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/** Round-trips binary content and tool messages without making a model request. */
public final class MessageRoundTrip {
    private MessageRoundTrip() {}

    public static Map<String, Object> restore() throws Exception {
        var original = MessageFixtures.imageQuestion();
        var revised = original.withText("Describe the attached image briefly.");
        if (!original.content().get(1).equals(revised.content().get(1))) {
            throw new IllegalStateException("Editing text must preserve image content");
        }
        var state = Map.<String, Object>of(
            "messages", MessageFixtures.toolConversation(),
            "attachments", List.of(revised));
        var serializer = new ArgiJacksonStateSerializer(OverAllState::new);
        var restored = serializer.dataFromBytes(serializer.dataToBytes(state));
        if (!state.equals(restored)) {
            throw new IllegalStateException("Neutral message content changed during checkpoint serialization");
        }
        return restored;
    }

    public static void main(String[] args) throws Exception {
        LoggerFactory.getLogger(MessageRoundTrip.class).info("Restored neutral messages: {}", restore());
    }
}
