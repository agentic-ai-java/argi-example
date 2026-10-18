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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import tools.jackson.databind.json.JsonMapper;

/**
 * Makes retrieval callable by the agent without sending the full corpus in every prompt.
 */
public final class DocumentationSearchTool implements Tool {

	private final DocumentationCorpus corpus;

	public DocumentationSearchTool(DocumentationCorpus corpus) {
		this.corpus = corpus;
	}

	@Override
	public ToolDefinition definition() {
		return new ToolDefinition("search_docs", "Search ARGI documentation and return citation labels", """
				{"type":"object","properties":{"query":{"type":"string","minLength":1}},
				 "required":["query"],"additionalProperties":false}
				""");
	}

	@Override
	public CompletionStage<ToolResult> execute(ToolCall call, ToolContext context) {
		var arguments = JsonMapper.shared().readTree(call.arguments());
		var query = arguments.path("query");
		if (!arguments.isObject() || arguments.size() != 1 || !query.isString() || query.asString().isBlank()) {
			return CompletableFuture.completedFuture(ToolResult.error("A non-empty query string is required"));
		}
		return CompletableFuture.completedFuture(ToolResult.text(corpus.evidence(query.asString())));
	}

}
