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
import io.github.agentic.ai.graph.OverAllState;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.checkpoint.config.SaverConfig;
import io.github.agentic.ai.graph.checkpoint.savers.file.FileSystemSaver;
import io.github.agentic.ai.graph.serializer.plain_text.jackson.ArgiJacksonStateSerializer;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Run with 'prepare' and then 'resume' to demonstrate process-independent recovery. */
public final class Persistence {

	private Persistence() {
	}

	public static CompiledGraph workflow(Path directory) throws Exception {
		var saver = FileSystemSaver.builder()
			.targetFolder(directory)
			.stateSerializer(new ArgiJacksonStateSerializer(OverAllState::new))
			.build();
		return new StateGraph().addNode("prepare", node_async(s -> Map.of("status", "pending")))
			.addNode("finish", node_async(s -> Map.of("status", "complete")))
			.addEdge(START, "prepare")
			.addEdge("prepare", "finish")
			.addEdge("finish", END)
			.compile(CompileConfig.builder()
				.saverConfig(SaverConfig.builder().register(saver).build())
				.interruptBefore("finish")
				.build());
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1 || !Set.of("prepare", "resume").contains(args[0])) {
			throw new IllegalArgumentException("Use prepare or resume");
		}
		var graph = workflow(Path.of("target", "checkpoints"));
		var config = RunnableConfig.builder().threadId("invoice-42").build();
		if (args[0].equals("prepare"))
			graph.invoke(Map.of("invoice", "INV-42"), config);
		else
			graph.invoke((Map<String, Object>) null, config.withResume());
		var log = org.slf4j.LoggerFactory.getLogger(Persistence.class);
		log.info("State: {}", graph.getState(config).state().data());
		log.info("Retained checkpoints: {}", graph.getStateHistory(config).size());
	}

}
