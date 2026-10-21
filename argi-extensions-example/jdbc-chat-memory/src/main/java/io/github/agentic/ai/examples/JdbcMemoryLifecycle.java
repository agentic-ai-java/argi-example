/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.agentic.ai.examples;

import java.util.List;
import java.util.UUID;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

/** Demonstrates window eviction, reopening, isolation and deletion using local H2. */
public final class JdbcMemoryLifecycle {

	private JdbcMemoryLifecycle() {
	}

	public static List<String> exercise(String url) {
		var memory = JdbcChatMemory.memory(url);
		String first = "window-" + UUID.randomUUID();
		String second = "isolated-" + UUID.randomUUID();
		try {
			memory.add(second, new UserMessage("Keep this conversation."));
			for (int turn = 1; turn <= 3; turn++) {
				memory.add(first, new UserMessage("Question " + turn));
				memory.add(first, new AssistantMessage("Answer " + turn));
			}
			var reopened = JdbcChatMemory.memory(url);
			var retained = reopened.get(first).stream().map(Message::getText).toList();
			if (!retained.equals(List.of("Question 2", "Answer 2", "Question 3", "Answer 3"))) {
				throw new IllegalStateException("Unexpected restored message window: " + retained);
			}
			reopened.clear(first);
			if (!reopened.get(first).isEmpty() || reopened.get(second).size() != 1) {
				throw new IllegalStateException("Clearing one conversation affected another conversation");
			}
			return retained;
		}
		finally {
			memory.clear(first);
			memory.clear(second);
		}
	}

	public static void main(String[] args) {
		LoggerFactory.getLogger(JdbcMemoryLifecycle.class)
			.info("Restored window: {}", exercise("jdbc:h2:mem:lifecycle;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE"));
	}

}
