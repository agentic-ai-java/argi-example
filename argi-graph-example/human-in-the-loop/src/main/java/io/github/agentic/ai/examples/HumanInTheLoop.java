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

import io.github.agentic.ai.graph.CompileConfig;
import io.github.agentic.ai.graph.CompiledGraph;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.checkpoint.config.SaverConfig;
import io.github.agentic.ai.graph.checkpoint.savers.MemorySaver;
import java.util.Map;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Pauses before a refund decision and resumes from the same saved thread. */
public final class HumanInTheLoop {

	private HumanInTheLoop() {
	}

	public static CompiledGraph workflow() throws Exception {
		return new StateGraph().addNode("prepare", node_async(state -> Map.of("status", "awaiting-approval")))
			.addNode("decide",
					node_async(state -> Map.of("status",
							state.<Boolean>value("approved").orElseThrow() ? "refunded" : "rejected")))
			.addEdge(START, "prepare")
			.addEdge("prepare", "decide")
			.addEdge("decide", END)
			.compile(CompileConfig.builder()
				.saverConfig(SaverConfig.builder().register(new MemorySaver()).build())
				.interruptBefore("decide")
				.build());
	}

	public static String refund(boolean approved) throws Exception {
		var workflow = workflow();
		var config = RunnableConfig.builder().threadId("refund-1042").build();
		workflow.invoke(Map.of("amount", 42), config);
		// Preserve the updated checkpoint cursor and explicitly resume execution.
		var resumed = workflow.updateState(config, Map.of("approved", approved)).withResume();
		return workflow.invoke((Map<String, Object>) null, resumed).orElseThrow().<String>value("status").orElseThrow();
	}

	public static void main(String[] args) throws Exception {
		org.slf4j.LoggerFactory.getLogger(HumanInTheLoop.class).info("{}", refund(true));
	}

}
