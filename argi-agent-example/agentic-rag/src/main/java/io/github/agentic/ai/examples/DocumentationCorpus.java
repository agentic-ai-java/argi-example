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
import java.util.Locale;
import java.util.stream.Collectors;

/** A small immutable corpus with stable citation IDs and explicit no-match behavior. */
public final class DocumentationCorpus {

	public record Page(String id, String text, List<String> keywords) {
		public Page {
			keywords = List.copyOf(keywords);
		}
	}

	private final List<Page> pages;

	public DocumentationCorpus(List<Page> pages) {
		this.pages = List.copyOf(pages);
	}

	public List<Page> search(String query) {
		if (query == null || query.isBlank()) {
			return List.of();
		}
		String normalized = query.toLowerCase(Locale.ROOT);
		return pages.stream()
			.filter(page -> page.keywords().stream().anyMatch(normalized::contains))
			.sorted(java.util.Comparator.comparing(Page::id))
			.toList();
	}

	public String evidence(String query) {
		var matches = search(query);
		return matches.isEmpty() ? "No matching source."
				: matches.stream().map(page -> "[" + page.id() + "] " + page.text()).collect(Collectors.joining("\n"));
	}

	public static DocumentationCorpus sample() {
		return new DocumentationCorpus(List.of(
				new Page("checkpoint", "A checkpoint records thread state and the next graph node.",
						List.of("checkpoint", "recover", "persist")),
				new Page("approval", "Resume approvals with the same thread and explicit human feedback.",
						List.of("approval", "approve", "human")),
				new Page("tool", "ARGI executes tools; model adapters expose only their definitions.",
						List.of("tool", "function"))));
	}

}
