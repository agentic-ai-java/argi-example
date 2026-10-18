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

import io.github.agentic.ai.graph.store.Store;
import io.github.agentic.ai.graph.store.StoreItem;
import io.github.agentic.ai.graph.store.StoreSearchRequest;
import io.github.agentic.ai.graph.store.StoreSearchResult;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Keeps namespace construction outside graph nodes and invocation metadata. */
public final class PreferenceRepository {

	private final Store store;

	public PreferenceRepository(Store store) {
		this.store = store;
	}

	public void save(String user, String key, Map<String, Object> value) {
		store.putItem(StoreItem.of(namespace(user), key, value));
	}

	public Optional<StoreItem> find(String user, String key) {
		return store.getItem(namespace(user), key);
	}

	public StoreSearchResult page(String user, int offset, int limit) {
		if (offset < 0 || limit < 1) {
			throw new IllegalArgumentException("Offset must be non-negative and limit must be positive");
		}
		return store.searchItems(StoreSearchRequest.builder()
			.namespace(namespace(user))
			.filter(Map.of("active", true))
			.sortFields(List.of("key"))
			.offset(offset)
			.limit(limit)
			.build());
	}

	public boolean delete(String user, String key) {
		return store.deleteItem(namespace(user), key);
	}

	private static List<String> namespace(String user) {
		if (user == null || user.isBlank()) {
			throw new IllegalArgumentException("A user namespace is required");
		}
		return List.of("users", user, "preferences");
	}

}
