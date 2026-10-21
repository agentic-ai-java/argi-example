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

import java.util.Map;

/** Validated domain input is converted to ordinary checkpoint-compatible data. */
public record SupportTicket(String id, String text) {
	public SupportTicket {
		if (id == null || id.isBlank() || text == null || text.isBlank()) {
			throw new IllegalArgumentException("Ticket ID and text must not be blank");
		}
	}

	public Map<String, Object> state() {
		return Map.of("ticket_id", id, "ticket", text);
	}
}
