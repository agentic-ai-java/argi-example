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

import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.graph.KeyStrategyFactory;
import io.github.agentic.ai.graph.OverAllState;
import io.github.agentic.ai.graph.StateGraph;
import io.github.agentic.ai.graph.serializer.plain_text.jackson.ArgiJacksonStateSerializer;
import io.github.agentic.ai.graph.state.strategy.AppendStrategy;
import io.github.agentic.ai.graph.state.strategy.ReplaceStrategy;
import java.util.List;
import java.util.Map;

import static io.github.agentic.ai.graph.StateGraph.END;
import static io.github.agentic.ai.graph.StateGraph.START;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Keeps model messages and checkpoint state independent of provider SDK classes. */
public final class StateSerialization {

	private StateSerialization() {
	}

	public static Map<String, Object> roundTrip() throws Exception {
		KeyStrategyFactory keys = () -> Map.of("events", new AppendStrategy(), "status", new ReplaceStrategy());
		var graph = new StateGraph(keys)
			.addNode("accept", node_async(s -> Map.of("events", List.of("accepted"), "status", "running")))
			.addNode("finish", node_async(s -> Map.of("events", List.of("finished"), "status", "done")))
			.addEdge(START, "accept")
			.addEdge("accept", "finish")
			.addEdge("finish", END)
			.compile();
		var state = graph.invoke(Map.of("events", List.of("received"), "messages", List.of(ChatMessage.user("Start"))))
			.orElseThrow();
		var serializer = new ArgiJacksonStateSerializer(OverAllState::new);
		return serializer.dataFromBytes(serializer.dataToBytes(state.data()));
	}

	public static void main(String[] args) throws Exception {
		org.slf4j.LoggerFactory.getLogger(StateSerialization.class).info("{}", roundTrip());
	}

}
