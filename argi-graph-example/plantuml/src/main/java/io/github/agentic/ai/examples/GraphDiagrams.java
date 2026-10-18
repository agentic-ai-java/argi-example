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

import io.github.agentic.ai.graph.GraphRepresentation;
import io.github.agentic.ai.graph.StateGraph;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Creates portable diagram source files; no renderer service or browser is required. */
public final class GraphDiagrams {

	private GraphDiagrams() {
	}

	public static void export(Path directory) throws Exception {
		var graph = new StateGraph().addNode("draft", node_async(s -> Map.of("draft", "Release notes")))
			.addNode("review", node_async(s -> Map.of("reviewed", true)))
			.addEdge(START, "draft")
			.addEdge("draft", "review")
			.addEdge("review", END);
		Files.createDirectories(directory);
		Files.writeString(directory.resolve("release.puml"),
				graph.getGraph(GraphRepresentation.Type.PLANTUML, "Release review", true).content());
		Files.writeString(directory.resolve("release.mmd"),
				graph.getGraph(GraphRepresentation.Type.MERMAID, "Release review", true).content());
	}

	public static void main(String[] args) throws Exception {
		export(Path.of("target", "diagrams"));
		org.slf4j.LoggerFactory.getLogger(GraphDiagrams.class)
			.info("Wrote target/diagrams/release.puml and release.mmd");
	}

}
