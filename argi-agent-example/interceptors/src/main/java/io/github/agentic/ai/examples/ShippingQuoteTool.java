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

import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.api.chat.ToolDefinition;
import io.github.agentic.ai.api.tool.Tool;
import io.github.agentic.ai.api.tool.ToolContext;
import io.github.agentic.ai.api.tool.ToolResult;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import tools.jackson.databind.json.JsonMapper;

/**
 * A fixed sample tariff gives tool selection a distinct alternative to order tracking.
 */
public final class ShippingQuoteTool implements Tool {

	@Override
	public ToolDefinition definition() {
		return new ToolDefinition("quote_shipping", "Read the sample shipping tariff for US or GB", """
				{"type":"object","properties":{"country":{"type":"string","enum":["US","GB"]}},
				 "required":["country"],"additionalProperties":false}
				""");
	}

	@Override
	public CompletionStage<ToolResult> execute(ToolCall call, ToolContext context) {
		var arguments = JsonMapper.shared().readTree(call.arguments());
		var country = arguments.path("country");
		if (!arguments.isObject() || arguments.size() != 1 || !country.isString()) {
			return CompletableFuture.completedFuture(ToolResult.error("Provide one country string"));
		}
		String quote = Map.of("US", "Sample tariff: USD 5", "GB", "Sample tariff: GBP 4").get(country.asString());
		return CompletableFuture.completedFuture(
				quote == null ? ToolResult.error("No sample tariff for this country") : ToolResult.text(quote));
	}

}
