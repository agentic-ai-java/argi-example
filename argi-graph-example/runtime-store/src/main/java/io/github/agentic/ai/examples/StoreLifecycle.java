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

import io.github.agentic.ai.graph.store.stores.MemoryStore;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates upsert, namespace isolation, filtered pagination and targeted deletion.
 */
public final class StoreLifecycle {

	private StoreLifecycle() {
	}

	public static void main(String[] args) {
		var repository = new PreferenceRepository(new MemoryStore());
		repository.save("ada", "language", Map.of("active", true, "value", "English"));
		repository.save("ada", "style", Map.of("active", true, "value", "concise"));
		repository.save("ada", "archived", Map.of("active", false, "value", "verbose"));
		repository.save("grace", "language", Map.of("active", true, "value", "French"));
		repository.save("ada", "language", Map.of("active", true, "value", "German"));
		var log = LoggerFactory.getLogger(StoreLifecycle.class);
		log.info("Updated Ada preference: {}", repository.find("ada", "language").orElseThrow().getValue());
		log.info("Independent Grace preference: {}", repository.find("grace", "language").orElseThrow().getValue());
		for (int offset = 0;; offset++) {
			var page = repository.page("ada", offset, 1);
			log.info("Page {} of {}: {}", page.getCurrentPage(), page.getTotalPages(), page.getItems());
			if (!page.hasMore()) {
				break;
			}
		}
		log.info("First deletion: {}", repository.delete("ada", "style"));
		log.info("Repeated deletion: {}", repository.delete("ada", "style"));
		log.info("Deleted preference present: {}", repository.find("ada", "style").isPresent());
	}

}
