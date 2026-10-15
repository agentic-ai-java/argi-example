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
import java.util.ArrayList;
import java.util.List;

/** Synthetic project history large enough to cross the configured summary threshold. */
public final class ProjectTranscript {
    private ProjectTranscript() {}

    public static List<ChatMessage> messages() {
        var messages = new ArrayList<ChatMessage>();
        messages.add(ChatMessage.user("Project invariant: preserve pending human approvals during recovery."));
        for (String topic : List.of("checkpoint ownership", "thread isolation", "shutdown",
                "rollback", "schema migration", "operator review", "tool failures", "release validation")) {
            String detail = ("For " + topic + ", record the decision, its owner, the evidence required "
                + "before rollout, and the recovery procedure if verification fails. ").repeat(10);
            messages.add(ChatMessage.user(detail));
            messages.add(ChatMessage.assistant("Recorded the requirements for " + topic + "."));
        }
        messages.add(ChatMessage.user("Summarize the decisions and preserve the original project invariant."));
        return List.copyOf(messages);
    }
}
