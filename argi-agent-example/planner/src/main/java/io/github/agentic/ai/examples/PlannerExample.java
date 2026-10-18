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

import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.graph.agent.planner.ArgiPlanner;
import io.github.agentic.ai.graph.agent.planner.PlanSpec;

/** Planning produces data; it never executes the capabilities named by the plan. */
public final class PlannerExample {

	private PlannerExample() {
	}

	public static PlanSpec plan(ChatClient client, ArgiPlanner.Mode mode) {
		var planner = new ArgiPlanner(client, PlanningCatalog.validator());
		return planner.plan(new ArgiPlanner.Request("Research checkpoint recovery and review the findings.", 1, mode,
				mode == ArgiPlanner.Mode.DIRECT ? "agent:research" : null));
	}

	public static void main(String[] args) {
		var mode = ArgiPlanner.Mode.valueOf(args.length == 0 ? "DECOMPOSE" : args[0]);
		org.slf4j.LoggerFactory.getLogger(PlannerExample.class)
			.info("{}", plan(SpringAiExample.client(System.getenv()), mode));
	}

}
