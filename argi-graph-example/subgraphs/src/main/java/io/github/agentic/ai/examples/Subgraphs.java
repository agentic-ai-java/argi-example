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
import io.github.agentic.ai.graph.StateGraph;
import java.util.Map;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Embeds an uncompiled StateGraph so the parent owns graph compilation. */
public final class Subgraphs {

	private Subgraphs() {
	}

	public static StateGraph normalization() throws Exception {
		return new StateGraph()
			.addNode("trim", node_async(s -> Map.of("customer", s.<String>value("customer").orElseThrow().strip())))
			.addNode("tax", node_async(s -> Map.of("total", s.<Integer>value("subtotal").orElseThrow() * 120 / 100)))
			.addEdge(START, "trim")
			.addEdge("trim", "tax")
			.addEdge("tax", END);
	}

	public static CompiledGraph workflow() throws Exception {
		return new StateGraph().addNode("normalize", normalization())
			.addNode("invoice", node_async(
					s -> Map.of("invoice", s.value("customer").orElseThrow() + ": " + s.value("total").orElseThrow())))
			.addEdge(START, "normalize")
			.addEdge("normalize", "invoice")
			.addEdge("invoice", END)
			.compile();
	}

	public static void main(String[] args) throws Exception {
		org.slf4j.LoggerFactory.getLogger(Subgraphs.class)
			.info("{}", workflow().invoke(Map.of("customer", " Ada ", "subtotal", 100)).orElseThrow().data());
	}

}
