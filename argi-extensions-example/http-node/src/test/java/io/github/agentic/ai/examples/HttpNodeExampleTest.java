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

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class HttpNodeExampleTest {
    @Test void executesRealHttpRequestAndRejectsPrivateAccessByDefault() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/orders/ORDER-42", exchange -> {
            byte[] body = "{\"status\":\"dispatched\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) { output.write(body); }
        });
        server.start();
        try {
            String base = "http://127.0.0.1:" + server.getAddress().getPort();
            var state = HttpNodeExample.fetch(base, true);
            assertTrue(state.get("tracking").toString().contains("dispatched"));
            assertThrows(Exception.class, () -> HttpNodeExample.fetch(base, false));
            try (var example = mockStatic(HttpNodeExample.class, CALLS_REAL_METHODS)) {
                example.when(() -> HttpNodeExample.fetch("http://127.0.0.1:8091", false)).thenReturn(state);
                HttpNodeExample.main(new String[0]);
            }
        } finally { server.stop(0); }
    }
}
