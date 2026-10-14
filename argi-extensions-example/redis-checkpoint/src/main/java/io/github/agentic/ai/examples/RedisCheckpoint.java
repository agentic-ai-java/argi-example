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


import io.github.agentic.ai.graph.*;
import io.github.agentic.ai.graph.checkpoint.config.SaverConfig;
import io.github.agentic.ai.graph.persistence.redis.RedisSaver;
import io.github.agentic.ai.graph.serializer.plain_text.jackson.ArgiJacksonStateSerializer;
import org.redisson.Redisson;
import org.redisson.config.Config;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static io.github.agentic.ai.graph.StateGraph.*;
import static io.github.agentic.ai.graph.action.AsyncNodeAction.node_async;

/** Uses a namespaced example thread and an expiring Redis checkpoint. */
public final class RedisCheckpoint {
    private RedisCheckpoint() {}
    public static void main(String[] args) throws Exception {
        var connection = new Config();
        connection.useSingleServer().setAddress(System.getenv().getOrDefault("REDIS_URL", "redis://127.0.0.1:6379"));
        String password = System.getenv("REDIS_PASSWORD");
        if (password != null && !password.isBlank()) connection.useSingleServer().setPassword(password);
        var client = Redisson.create(connection);
        try {
            var saver = RedisSaver.builder().redisson(client).ttl(1, TimeUnit.HOURS)
                .stateSerializer(new ArgiJacksonStateSerializer(OverAllState::new)).build();
            var graph = new StateGraph().addNode("save", node_async(s -> Map.of("answer", 42)))
                .addEdge(START, "save").addEdge("save", END)
                .compile(CompileConfig.builder().saverConfig(SaverConfig.builder().register(saver).build()).build());
            var config = RunnableConfig.builder().threadId("argi-example-" + UUID.randomUUID()).build();
            graph.invoke(Map.of(), config);
            org.slf4j.LoggerFactory.getLogger(RedisCheckpoint.class).info("{}", saver.get(config).orElseThrow().getState());
        } finally { client.shutdown(); }
    }
}
