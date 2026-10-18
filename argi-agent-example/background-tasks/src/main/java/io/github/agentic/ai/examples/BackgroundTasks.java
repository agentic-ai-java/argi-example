/*
 * Copyright 2025-2026 the original author or authors.
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

import io.github.agentic.ai.graph.agent.tools.task.BackgroundTask;
import io.github.agentic.ai.graph.agent.tools.task.BoundedTaskRepository;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Uses a completion gate to demonstrate capacity rejection without timing-dependent sleeps. */
public final class BackgroundTasks {
    private static final Logger LOG = LoggerFactory.getLogger(BackgroundTasks.class);

    private BackgroundTasks() {
    }

    public record Report(boolean capacityRejected, boolean duplicateRejected, String first, String queued) {
    }

    public static Report run() throws InterruptedException {
        final CompletableFuture<String> release = new CompletableFuture<>();
        try (BoundedTaskRepository tasks = new BoundedTaskRepository(1, 1, 2, Duration.ofMinutes(1))) {
            final BackgroundTask first = tasks.putTask("invoice-1", release::join);
            try {
                final BackgroundTask queued = tasks.putTask("invoice-2", () -> "second invoice checked");
                final boolean full = rejected(tasks, "invoice-3");
                final boolean duplicate = rejected(tasks, "invoice-1");
                release.complete("first invoice checked");
                if (!first.waitForCompletion(5000) || !queued.waitForCompletion(5000)) {
                    throw new IllegalStateException("Background tasks did not complete within five seconds");
                }
                final Report result = new Report(full, duplicate, first.getResult(), queued.getResult());
                tasks.removeTask("invoice-1");
                return result;
            }
            finally {
                // Never leave the owned executor blocked if observation fails.
                release.complete("released during cleanup");
            }
        }
    }

    private static boolean rejected(BoundedTaskRepository tasks, String id) {
        try {
            tasks.putTask(id, () -> "unexpected admission");
            return false;
        }
        catch (RejectedExecutionException expected) {
            return true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LOG.info("Bounded background execution: {}", run());
    }
}
