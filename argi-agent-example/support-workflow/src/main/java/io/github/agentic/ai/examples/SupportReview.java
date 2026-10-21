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

import io.github.agentic.ai.graph.CompiledGraph;
import io.github.agentic.ai.graph.RunnableConfig;
import java.util.Map;

/** Keeps a review decision attached to the saved draft and its checkpoint cursor. */
public final class SupportReview {

	private final CompiledGraph graph;

	public SupportReview(CompiledGraph graph) {
		this.graph = graph;
	}

	public RunnableConfig submit(SupportTicket ticket) {
		var config = RunnableConfig.builder().threadId(ticket.id()).build();
		graph.invoke(ticket.state(), config);
		return config;
	}

	public Map<String, Object> draft(RunnableConfig config) {
		return Map.copyOf(graph.getState(config).state().data());
	}

	public Map<String, Object> decide(RunnableConfig config, boolean approved, String reviewer) throws Exception {
		if (reviewer == null || reviewer.isBlank()) {
			throw new IllegalArgumentException("A reviewer is required");
		}
		if (!"review".equals(graph.getState(config).next())) {
			throw new IllegalStateException("The ticket is not awaiting review");
		}
		var cursor = graph.updateState(config, Map.of("approved", approved, "reviewer", reviewer)).withResume();
		return graph.invoke((Map<String, Object>) null, cursor).orElseThrow().data();
	}

	public static void main(String[] args) throws Exception {
		var review = new SupportReview(SupportWorkflow.workflow(SpringAiExample.client(System.getenv())));
		var approved = review.submit(new SupportTicket("ticket-approved", "Explain how to request a refund."));
		var rejected = review.submit(new SupportTicket("ticket-rejected", "Help me debug a checkpoint failure."));
		var log = org.slf4j.LoggerFactory.getLogger(SupportReview.class);
		log.info("Draft awaiting review: {}", review.draft(approved));
		log.info("Approved: {}", review.decide(approved, true, "operator-a"));
		log.info("Rejected: {}", review.decide(rejected, false, "operator-b"));
	}

}
