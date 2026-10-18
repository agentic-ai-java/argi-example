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

import io.github.agentic.ai.graph.execution.definition.DefinitionKind;
import io.github.agentic.ai.graph.execution.definition.DefinitionReference;
import io.github.agentic.ai.graph.execution.definition.DefinitionRegistry;
import io.github.agentic.ai.graph.execution.definition.ResolvedRunDefinitions;
import io.github.agentic.ai.graph.execution.definition.RunDefinitionAdmission;
import io.github.agentic.ai.graph.execution.definition.RunDefinitionRecord;
import io.github.agentic.ai.graph.execution.definition.RunDefinitionRecordCodec;
import io.github.agentic.ai.graph.execution.definition.RuntimeComponentDeclaration;
import io.github.agentic.ai.graph.execution.definition.RuntimeComponentRole;
import io.github.agentic.ai.graph.execution.definition.WorkerCapabilities;
import io.github.agentic.ai.graph.execution.run.MemoryRunStore;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Resolves recorded versions before dispatch; does not claim to implement a durable worker. */
public final class VersionedRecovery {
    private static final Logger LOG = LoggerFactory.getLogger(VersionedRecovery.class);
    public static final DefinitionReference GRAPH_V1 = DefinitionReference.of(DefinitionKind.GRAPH, "invoice", "1");
    public static final DefinitionReference GRAPH_V2 = DefinitionReference.of(DefinitionKind.GRAPH, "invoice", "2");
    public static final DefinitionReference TOOLS = DefinitionReference.of(DefinitionKind.TOOL_CATALOG, "billing", "1");
    public static final DefinitionReference CODEC = DefinitionReference.of(DefinitionKind.CODEC, "billing-json", "1");
    public static final DefinitionReference APP = DefinitionReference.of(DefinitionKind.APPLICATION, "billing", "1");

    private VersionedRecovery() {
    }

    public static RunDefinitionRecord record() {
        return RunDefinitionRecord.of(GRAPH_V1, TOOLS, CODEC, APP);
    }

    public static DefinitionRegistry registry(boolean retainV1) {
        final DefinitionRegistry.Builder builder = DefinitionRegistry.builder()
                .register(GRAPH_V2, List.of("readInvoice", "checkTax"))
                .register(TOOLS, List.of("readInvoice"))
                .register(CODEC, "billing-json-v1")
                .register(APP, "billing-v1");
        if (retainV1) {
            builder.register(GRAPH_V1, List.of("readInvoice"));
        }
        return builder.build();
    }

    public static ResolvedRunDefinitions restore(DefinitionRegistry registry, Set<DefinitionReference> readableCodecs) {
        final RunDefinitionRecordCodec codec = new RunDefinitionRecordCodec();
        final RunDefinitionRecord restored = codec.decode(codec.encode(record()));
        return RunDefinitionAdmission.resolve(restored, registry,
                new WorkerCapabilities(1, Set.of(DefinitionKind.values()), readableCodecs));
    }

    public static List<RuntimeComponentDeclaration> localComponents() {
        return Arrays.stream(RuntimeComponentRole.values())
                .map(role -> role == RuntimeComponentRole.BACKGROUND_TOOL
                        ? RuntimeComponentDeclaration.instanceBound(role, "local-background-tasks")
                        : RuntimeComponentDeclaration.notApplicable(role))
                .toList();
    }

    public static void rejectMemoryDurability() {
        RunDefinitionAdmission.requireDurableMode(new MemoryRunStore().capabilities(), localComponents());
    }

    public static void main(String[] args) {
        LOG.info("Recorded graph after upgrade: {}", restore(registry(true), Set.of(CODEC)).graph());
        try {
            restore(registry(false), Set.of(CODEC));
        }
        catch (io.github.agentic.ai.graph.execution.definition.DefinitionResolutionException expected) {
            LOG.info("Missing recorded version rejected: {}", expected.code());
        }
        try {
            rejectMemoryDurability();
        }
        catch (IllegalStateException expected) {
            LOG.info("Memory backend rejected for durable mode: {}", expected.getMessage());
        }
    }
}
