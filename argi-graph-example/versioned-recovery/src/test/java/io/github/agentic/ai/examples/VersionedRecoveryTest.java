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

import io.github.agentic.ai.graph.execution.definition.CodecIncompatibilityException;
import io.github.agentic.ai.graph.execution.definition.DefinitionFailureCode;
import io.github.agentic.ai.graph.execution.definition.DefinitionResolutionException;
import io.github.agentic.ai.graph.execution.definition.RecoveryCapabilityException;
import io.github.agentic.ai.graph.execution.definition.RunDefinitionAdmission;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VersionedRecoveryTest {
    @Test
    void existingRunKeepsItsRecordedVersionAfterDeployment() {
        final var resolved = VersionedRecovery.restore(VersionedRecovery.registry(true), Set.of(VersionedRecovery.CODEC));
        assertEquals(VersionedRecovery.GRAPH_V1, resolved.graphReference());
        assertEquals(List.of("readInvoice"), resolved.graph());
        assertEquals(List.of("readInvoice"), resolved.toolCatalog());
    }

    @Test
    void missingOldDefinitionNeverFallsBackToNewVersion() {
        final var failure = assertThrows(DefinitionResolutionException.class,
                () -> VersionedRecovery.restore(VersionedRecovery.registry(false), Set.of(VersionedRecovery.CODEC)));
        assertEquals(DefinitionFailureCode.NOT_REGISTERED, failure.code());
        assertEquals(VersionedRecovery.GRAPH_V1, failure.reference());
    }

    @Test
    void unsupportedCodecAndInstanceBoundWorkAreRejected() {
        assertThrows(CodecIncompatibilityException.class,
                () -> VersionedRecovery.restore(VersionedRecovery.registry(true), Set.of()));
        assertThrows(RecoveryCapabilityException.class,
                () -> RunDefinitionAdmission.requireRecoverableComponents(VersionedRecovery.localComponents()));
        assertThrows(IllegalStateException.class, VersionedRecovery::rejectMemoryDurability);
        VersionedRecovery.main(new String[0]);
    }
}
