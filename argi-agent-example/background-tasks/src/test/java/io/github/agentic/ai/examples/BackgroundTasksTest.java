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

import io.github.agentic.ai.graph.agent.tools.task.BoundedTaskRepository;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(15)
class BackgroundTasksTest {
    @Test
    void enforcesCapacityAndDuplicateIdsThenDrainsTheQueue() throws Exception {
        final var report = BackgroundTasks.run();
        assertTrue(report.capacityRejected());
        assertTrue(report.duplicateRejected());
        assertEquals("first invoice checked", report.first());
        assertEquals("second invoice checked", report.queued());
        BackgroundTasks.main(new String[0]);
    }

    @Test
    void removingActiveWorkHidesItWithoutFreeingItsSlot() {
        final CompletableFuture<String> release = new CompletableFuture<>();
        try (var tasks = new BoundedTaskRepository(1, 0, 1, Duration.ofMinutes(1))) {
            tasks.putTask("active", release::join);
            try {
                tasks.removeTask("active");
                assertNull(tasks.getTask("active"));
                assertThrows(RejectedExecutionException.class, () -> tasks.putTask("next", () -> "next"));
                assertThrows(RejectedExecutionException.class, () -> tasks.putTask("active", () -> "duplicate"));
            }
            finally {
                release.complete("finished");
            }
        }
    }
}
