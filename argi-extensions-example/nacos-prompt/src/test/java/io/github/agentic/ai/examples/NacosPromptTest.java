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

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NacosPromptTest {
    @Test void listenerUpdatesTheTemplateAndClosesResources() throws Exception {
        var service = mock(ConfigService.class);
        when(service.getConfigAndSignListener(anyString(), anyString(), anyLong(), any())).thenReturn("[{\"name\":\"support\",\"template\":\"Explain {question}.\"}]");
        try (var prompts = new NacosPrompt(service)) {
            assertEquals("Explain recovery.", prompts.render("recovery"));
            var listener = ArgumentCaptor.forClass(Listener.class);
            verify(service).getConfigAndSignListener(eq("argi.configurable.prompt"), eq("DEFAULT_GROUP"), eq(5000L), listener.capture());
            assertNull(listener.getValue().getExecutor());
            listener.getValue().receiveConfigInfo("[{\"name\":\"support\",\"template\":\"Document {question} carefully.\"}]");
            assertEquals("Document recovery carefully.", prompts.render("recovery"));
        }
        verify(service).removeListener(eq("argi.configurable.prompt"), eq("DEFAULT_GROUP"), any());
        verify(service).shutDown();
        try (var nacos = mockStatic(NacosFactory.class)) {
            nacos.when(() -> NacosFactory.createConfigService(any(java.util.Properties.class))).thenReturn(service);
            NacosPrompt.main(new String[0]);
        }
        when(service.getConfigAndSignListener(anyString(), anyString(), anyLong(), any())).thenReturn(null);
        try (var prompts = new NacosPrompt(service)) { assertEquals("Answer recovery briefly.", prompts.render("recovery")); }
    }
}
