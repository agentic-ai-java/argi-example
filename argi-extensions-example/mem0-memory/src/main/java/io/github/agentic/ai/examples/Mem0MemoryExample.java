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

import io.github.agentic.ai.memory.mem0.model.Mem0ServerRequest;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/** Mem0 semantic memory is separate from an ordered chat-message repository. */
@SpringBootApplication
public class Mem0MemoryExample {

	@Bean
	ApplicationRunner memories(org.springframework.ai.vectorstore.VectorStore store) {
		return args -> {
			String user = "argi-example-user";
			store.add(List
				.of(new Document("I prefer English explanations with Java examples.", Map.of("user_id", user))));
			var request = Mem0ServerRequest.SearchRequest.mem0Builder()
				.query("What language and examples does the user prefer?")
				.userId(user)
				.build();
			org.slf4j.LoggerFactory.getLogger(Mem0MemoryExample.class).info("{}", store.similaritySearch(request));
		};
	}

	public static void main(String[] args) {
		try (var context = new SpringApplicationBuilder(Mem0MemoryExample.class).web(WebApplicationType.NONE)
			.run(args)) {
			org.slf4j.LoggerFactory.getLogger(Mem0MemoryExample.class).info("Semantic memory request completed");
		}
	}

}
