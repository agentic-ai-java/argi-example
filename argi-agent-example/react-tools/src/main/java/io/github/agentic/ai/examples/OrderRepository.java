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
import java.util.Optional;

/** A local fixture repository; replacing it does not change the tool schema. */
public final class OrderRepository {

	private final Map<String, OrderRecord> orders;

	public OrderRepository(Map<String, OrderRecord> orders) {
		this.orders = Map.copyOf(orders);
	}

	public Optional<OrderRecord> find(String id) {
		return Optional.ofNullable(orders.get(id));
	}

	public static OrderRepository sample() {
		return new OrderRepository(Map.of("ORDER-42", new OrderRecord("ORDER-42", "Dispatched; delivery on Friday"),
				"ORDER-43", new OrderRecord("ORDER-43", "Preparing shipment")));
	}

}
