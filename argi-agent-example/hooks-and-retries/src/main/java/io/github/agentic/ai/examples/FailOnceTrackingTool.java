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
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

/** An explicit fault-injection fixture for a read-only lookup. */
public final class FailOnceTrackingTool implements Tool {

	private final Tool delegate = ReactTools.trackingTool();

	private final AtomicInteger attempts = new AtomicInteger();

	@Override
	public ToolDefinition definition() {
		return delegate.definition();
	}

	@Override
	public CompletionStage<ToolResult> execute(ToolCall call, ToolContext context) {
		if (attempts.incrementAndGet() == 1) {
			return CompletableFuture.failedFuture(new IOException("Injected transient tracking failure"));
		}
		return delegate.execute(call, context);
	}

	public int attempts() {
		return attempts.get();
	}

}
