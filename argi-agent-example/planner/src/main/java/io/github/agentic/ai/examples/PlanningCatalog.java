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

import io.github.agentic.ai.graph.agent.planner.PlanCapability;
import io.github.agentic.ai.graph.agent.planner.PlanValidator;
import java.util.List;

/** The application declares executable capability names before asking a model to plan. */
public final class PlanningCatalog {

	private PlanningCatalog() {
	}

	public static final String CONTRACT = "{\"type\":\"object\",\"properties\":{},\"additionalProperties\":false}";

	public static PlanValidator validator() {
		return new PlanValidator(
				List.of(new PlanCapability("agent:research", "Research documentation", CONTRACT, CONTRACT),
						new PlanCapability("agent:review", "Review findings for completeness", CONTRACT, CONTRACT)),
				8, 4);
	}

}
