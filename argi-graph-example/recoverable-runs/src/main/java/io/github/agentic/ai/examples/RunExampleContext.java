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

import io.github.agentic.ai.graph.execution.ExecutionBudget;
import io.github.agentic.ai.graph.execution.PrincipalReference;
import io.github.agentic.ai.graph.execution.TenantReference;
import io.github.agentic.ai.graph.execution.TrustedExecutionContext;
import io.github.agentic.ai.graph.execution.run.RunPayload;
import io.github.agentic.ai.graph.execution.run.RunSpec;
import java.time.Clock;
import java.time.Duration;
import java.util.Map;

/**
 * Application-owned identity fixture; no model-supplied metadata is trusted as identity.
 */
public final class RunExampleContext {

	private RunExampleContext() {
	}

	public static TrustedExecutionContext authenticated(String runId, Clock clock) {
		// A real application constructs this context after authenticating its caller.
		return TrustedExecutionContext.builder()
			.tenant(new TenantReference("local-example", "tenant-a"))
			.principal(new PrincipalReference("local-example", "operator-a"))
			.runId(runId)
			.correlationId("example-request-42")
			.deadline(clock.instant().plus(Duration.ofMinutes(5)))
			.budget(ExecutionBudget.of(Map.of("modelCalls", 2L)))
			.build();
	}

	public static RunSpec specification(TrustedExecutionContext context, String session, String submissionKey) {
		return RunSpec.from(context, "argi-example", session, submissionKey, "invoice-review", "1",
				new RunPayload(1, "invoice.input", "{\"invoice\":\"INV-42\",\"amount\":42}"));
	}

}
