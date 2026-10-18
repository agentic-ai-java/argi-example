/*
 * Copyright 2025-2026 the original author or authors.
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
import io.github.agentic.ai.graph.OverAllState;
import io.github.agentic.ai.graph.agent.flow.agent.loop.ArrayLoopStrategy;
import io.github.agentic.ai.graph.agent.flow.agent.loop.LoopMode;
import io.github.agentic.ai.graph.agent.flow.agent.loop.LoopStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Inspects the actual array-loop dispatcher without requiring a model-backed child agent. */
public final class BatchedArrayLoop {
    private static final Logger LOG = LoggerFactory.getLogger(BatchedArrayLoop.class);

    private BatchedArrayLoop() {
    }

    public static List<String> batches(String json, int batchSize) {
        return dispatch(json, LoopMode.array(batchSize));
    }

    public static List<String> legacyElements(String json) {
        return dispatch(json, new ArrayLoopStrategy());
    }

    private static List<String> dispatch(String json, ArrayLoopStrategy strategy) {
        final Map<String, Object> data = new HashMap<>();
        data.put(LoopStrategy.MESSAGE_KEY, List.of(ChatMessage.user(json)));
        data.putAll(strategy.loopInit(new OverAllState(data)));
        final List<String> messages = new ArrayList<>();
        while (Boolean.TRUE.equals(data.get(strategy.loopFlagKey()))) {
            final Map<String, Object> next = strategy.loopDispatch(new OverAllState(data));
            data.putAll(next);
            if (Boolean.TRUE.equals(next.get(strategy.loopFlagKey()))) {
                messages.add(((ChatMessage) next.get(LoopStrategy.MESSAGE_KEY)).text());
            }
        }
        return List.copyOf(messages);
    }

    public static void main(String[] args) {
        LOG.info("Batches: {}", batches("[1,2,3,4,5]", 2));
        LOG.info("Explicit batch size one: {}", batches("[1,2]", 1));
        LOG.info("Legacy element framing: {}", legacyElements("[1,2]"));
    }
}
