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

import io.github.agentic.ai.graph.OverAllState;
import java.util.List;
import java.util.Map;

/** Independent local business checks write one result each through an append reducer. */
public final class OrderChecks {

	private OrderChecks() {
	}

	public static Map<String, Object> stock(OverAllState state) {
		return result("stock", state.<Boolean>value("in_stock").orElseThrow());
	}

	public static Map<String, Object> credit(OverAllState state) {
		return result("credit", state.<Boolean>value("credit_approved").orElseThrow());
	}

	public static Map<String, Object> shipping(OverAllState state) {
		return result("shipping", state.<Boolean>value("shipping_supported").orElseThrow());
	}

	private static Map<String, Object> result(String name, boolean passed) {
		return Map.of("checks", List.of(Map.of("name", name, "passed", passed)));
	}

	public static Map<String, Object> join(OverAllState state) {
		List<Map<String, Object>> checks = state.<List<Map<String, Object>>>value("checks").orElseThrow();
		boolean ready = checks.size() == 3
				&& checks.stream().allMatch(check -> Boolean.TRUE.equals(check.get("passed")));
		return Map.of("ready", ready);
	}

}
