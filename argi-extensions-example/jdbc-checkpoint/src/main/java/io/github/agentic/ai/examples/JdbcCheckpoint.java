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
import io.github.agentic.ai.graph.persistence.jdbc.h2.H2Saver;
import io.github.agentic.ai.graph.serializer.plain_text.jackson.ArgiJacksonStateSerializer;
import java.util.Map;
import org.h2.jdbcx.JdbcDataSource;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/**
 * Rebuilds the runtime and restores a pending approval from the Extensions JDBC saver.
 */
public final class JdbcCheckpoint {

	private JdbcCheckpoint() {
	}

	public static CompiledGraph workflow(String url) throws Exception {
		var source = new JdbcDataSource();
		source.setURL(url);
		var saver = H2Saver.builder()
			.dataSource(source)
			.stateSerializer(new ArgiJacksonStateSerializer(OverAllState::new))
			.build();
		return new StateGraph().addNode("quote", node_async(state -> Map.of("total", 125)))
			.addNode("confirm",
					node_async(state -> Map.of("status",
							state.<Boolean>value("approved").orElseThrow() ? "confirmed" : "cancelled")))
			.addEdge(START, "quote")
			.addEdge("quote", "confirm")
			.addEdge("confirm", END)
			.compile(CompileConfig.builder()
				.saverConfig(SaverConfig.builder().register(saver).build())
				.interruptBefore("confirm")
				.build());
	}

	public static String run(String url, boolean approved) throws Exception {
		var config = RunnableConfig.builder().threadId("booking-42").build();
		workflow(url).invoke(Map.of("customer", "Ada"), config);
		// A fresh saver and graph read the checkpoint through JDBC, without sharing a
		// memory saver.
		var restored = workflow(url);
		var resumed = restored.updateState(config, Map.of("approved", approved)).withResume();
		return restored.invoke((Map<String, Object>) null, resumed).orElseThrow().<String>value("status").orElseThrow();
	}

	public static void main(String[] args) throws Exception {
		org.slf4j.LoggerFactory.getLogger(JdbcCheckpoint.class)
			.info("{}", run("jdbc:h2:mem:booking;DB_CLOSE_DELAY=-1", true));
	}

}
