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

import io.agentscope.runtime.sandbox.box.BaseSandbox;
import io.agentscope.runtime.sandbox.manager.ManagerConfig;
import io.agentscope.runtime.sandbox.manager.SandboxService;
import io.github.agentic.ai.adapter.springai.SpringAiToolAdapter;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.sandbox.ToolkitInit;
import java.util.Objects;

/** Requires a separately provisioned AgentScope Runtime sandbox manager. */
public final class SandboxToolsExample {

	private SandboxToolsExample() {
	}

	public static void main(String[] args) throws Exception {
		String url = Objects.requireNonNull(System.getenv("SANDBOX_MANAGER_URL"), "Set SANDBOX_MANAGER_URL");
		var config = ManagerConfig.builder().baseUrl(url).bearerToken(System.getenv("SANDBOX_MANAGER_TOKEN")).build();
		try (var manager = new SandboxService(config)) {
			// Remote mode uses the manager URL; start() would initialize a local
			// container client.
			try (var sandbox = new BaseSandbox(manager, "argi-example", "calculation")) {
				var tools = SpringAiToolAdapter.fromCallbacks(ToolkitInit.RunPythonCodeTool(sandbox));
				var agent = ReactAgent.builder()
					.throwOnModelError(true)
					.name("sandbox_calculator")
					.chatClient(SpringAiExample.client(System.getenv()))
					.toolRegistry(tools)
					.instruction("Use Python only to calculate the requested expression.")
					.build();
				org.slf4j.LoggerFactory.getLogger(SandboxToolsExample.class)
					.info("{}", agent.call("Calculate sum([19, 23]).").text());
			}
		}
	}

}
