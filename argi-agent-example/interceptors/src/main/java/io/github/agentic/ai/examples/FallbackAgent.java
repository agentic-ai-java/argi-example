/*
 * Copyright 2026 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy at https://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package io.github.agentic.ai.examples;

import io.github.agentic.ai.api.chat.ChatClient;
import io.github.agentic.ai.graph.agent.ReactAgent;
import io.github.agentic.ai.graph.agent.interceptor.modelfallback.ModelFallbackInterceptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/** Configures a separate fallback model while preserving the neutral request pipeline. */
public final class FallbackAgent {
    private FallbackAgent() {}

    public static ReactAgent agent(ChatClient primary, ChatClient fallback) {
        return ReactAgent.builder().throwOnModelError(true).name("fallback_support").chatClient(primary)
            .instruction("Explain checkpoint recovery briefly.")
            .interceptors(List.of(ModelFallbackInterceptor.builder().addFallbackClient(fallback).build()))
            .build();
    }

    public static Map<String, String> fallbackEnvironment(Map<String, String> environment) {
        var fallback = new HashMap<>(environment);
        String model = environment.get("ARGI_FALLBACK_MODEL");
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Set ARGI_FALLBACK_MODEL before running this example");
        }
        fallback.put("ARGI_MODEL", model);
        for (String name : List.of("ARGI_API_KEY", "ARGI_BASE_URL")) {
            String alternate = environment.get(name.replace("ARGI_", "ARGI_FALLBACK_"));
            if (alternate != null && !alternate.isBlank()) {
                fallback.put(name, alternate);
            }
        }
        return Map.copyOf(fallback);
    }

    public static void main(String[] args) throws Exception {
        var environment = System.getenv();
        var primary = SpringAiExample.client(environment);
        var fallback = SpringAiExample.client(fallbackEnvironment(environment));
        LoggerFactory.getLogger(FallbackAgent.class).info("{}", agent(primary, fallback)
            .call("How can a paused graph resume?").text());
    }
}
