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

import io.github.agentic.ai.prompt.*;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/** Subscribes to the Extensions prompt JSON format and always renders the latest template. */
public final class NacosPrompt implements AutoCloseable {
    private static final String DATA_ID = "argi.configurable.prompt";
    private static final String GROUP = "DEFAULT_GROUP";
    private final ConfigService service;
    private final LiveFactory factory = new LiveFactory();
    private final Listener listener = new Listener() {
        @Override public Executor getExecutor() { return null; }
        @Override public void receiveConfigInfo(String content) { update(content); }
    };
    public NacosPrompt(ConfigService service) throws Exception {
        this.service = service;
        factory.create("support", "Answer {question} briefly.");
        String initial = service.getConfigAndSignListener(DATA_ID, GROUP, 5000, listener);
        if (initial != null && !initial.isBlank()) update(initial);
    }
    public void update(String content) {
        List<ConfigurablePromptTemplateFactory.ConfigurablePromptTemplateModel> models =
            JsonMapper.shared().readValue(content, new TypeReference<List<ConfigurablePromptTemplateFactory.ConfigurablePromptTemplateModel>>() {});
        factory.replace(models);
    }
    public String render(String question) {
        // Resolve from the factory on every call: a cached template would miss updates.
        return factory.getTemplate("support").render(Map.of("question", question));
    }
    @Override public void close() throws Exception {
        service.removeListener(DATA_ID, GROUP, listener);
        service.shutDown();
    }
    private static final class LiveFactory extends ConfigurablePromptTemplateFactory {
        private LiveFactory() { super(new PromptTemplateBuilderConfigure()); }
        void replace(List<ConfigurablePromptTemplateModel> models) { onConfigChange(models); }
    }
    public static void main(String[] args) throws Exception {
        var properties = new Properties();
        properties.setProperty("serverAddr", System.getenv().getOrDefault("NACOS_SERVER_ADDR", "127.0.0.1:8848"));
        properties.setProperty("username", System.getenv().getOrDefault("NACOS_USERNAME", ""));
        properties.setProperty("password", System.getenv().getOrDefault("NACOS_PASSWORD", ""));
        try (var prompts = new NacosPrompt(NacosFactory.createConfigService(properties))) {
            org.slf4j.LoggerFactory.getLogger(NacosPrompt.class).info("{}", prompts.render("How do I resume a workflow?"));
        }
    }
}
