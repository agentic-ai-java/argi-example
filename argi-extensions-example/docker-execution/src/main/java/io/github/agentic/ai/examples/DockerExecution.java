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

import io.github.agentic.ai.graph.node.code.CodeExecutor;
import io.github.agentic.ai.graph.node.code.docker.DockerCodeExecutor;
import io.github.agentic.ai.graph.node.code.entity.CodeBlock;
import io.github.agentic.ai.graph.node.code.entity.CodeExecutionConfig;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** Executes a fixed Python calculation through the Extensions Docker executor. */
public final class DockerExecution {

	private DockerExecution() {
	}

	public static CodeExecutionConfig config(Map<String, String> env) {
		return new CodeExecutionConfig().setDocker(env.getOrDefault("ARGI_DOCKER_IMAGE", "python:3.12.12-alpine3.22"))
			.setDockerHost(env.getOrDefault("DOCKER_HOST", "unix:///var/run/docker.sock"))
			.setWorkDir(Path.of("target", "code-workspace").toAbsolutePath().toString())
			.setTimeout(15)
			.setMaxOutputBytes(4096);
	}

	public static String run(CodeExecutor executor, CodeExecutionConfig config) throws Exception {
		var result = executor.executeCodeBlocks(List.of(new CodeBlock("python", "print(sum([19, 23]))")), config);
		if (result.exitCode() != 0)
			throw new IllegalStateException("Code execution failed: " + result.logs());
		return result.logs();
	}

	public static void main(String[] args) throws Exception {
		org.slf4j.LoggerFactory.getLogger(DockerExecution.class)
			.info("{}", run(new DockerCodeExecutor(), config(System.getenv())));
	}

}
