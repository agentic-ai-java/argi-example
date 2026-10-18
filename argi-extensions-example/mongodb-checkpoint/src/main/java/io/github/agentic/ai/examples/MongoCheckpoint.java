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

import com.mongodb.client.MongoClients;
import io.github.agentic.ai.graph.CompileConfig;
import io.github.agentic.ai.graph.OverAllState;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.checkpoint.config.SaverConfig;
import io.github.agentic.ai.graph.persistence.mongodb.MongoSaver;
import io.github.agentic.ai.graph.serializer.plain_text.jackson.ArgiJacksonStateSerializer;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Uses an existing MongoDB service and a unique example thread. */
public final class MongoCheckpoint {

	private MongoCheckpoint() {
	}

	public static void main(String[] args) throws Exception {
		try (var client = MongoClients
			.create(Objects.requireNonNull(System.getenv("MONGODB_URI"), "Set MONGODB_URI"))) {
			var saver = MongoSaver.builder()
				.client(client)
				.stateSerializer(new ArgiJacksonStateSerializer(OverAllState::new))
				.build();
			var graph = new StateGraph().addNode("save", node_async(s -> Map.of("answer", 42)))
				.addEdge(START, "save")
				.addEdge("save", END)
				.compile(CompileConfig.builder().saverConfig(SaverConfig.builder().register(saver).build()).build());
			var config = RunnableConfig.builder().threadId("argi-example-" + UUID.randomUUID()).build();
			graph.invoke(Map.of(), config);
			org.slf4j.LoggerFactory.getLogger(MongoCheckpoint.class)
				.info("{}", saver.get(config).orElseThrow().getState());
			var reader = MongoSaver.builder()
				.client(client)
				.stateSerializer(new ArgiJacksonStateSerializer(OverAllState::new))
				.build();
			org.slf4j.LoggerFactory.getLogger(MongoCheckpoint.class)
				.info("Recovery: {}", MongoRecovery.exercise(saver, reader));
		}
	}

}
