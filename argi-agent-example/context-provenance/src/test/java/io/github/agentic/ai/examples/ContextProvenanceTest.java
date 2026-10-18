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

import io.github.agentic.ai.api.chat.ChatSourceKind;
import io.github.agentic.ai.api.chat.ChatTrustLevel;
import io.github.agentic.ai.api.chat.MessageProvenance;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextProvenanceTest {
    @Test
    void externalContentNeverBecomesApplicationAuthority() {
        final var history = ContextProvenance.compact();
        assertTrue(MessageProvenance.isTrustedInstruction(history.instruction()));
        assertFalse(MessageProvenance.isTrustedInstruction(history.evidence()));
        assertEquals(ChatSourceKind.CHILD_OUTPUT, MessageProvenance.sourceOf(history.child()).orElseThrow());
        assertEquals(ChatTrustLevel.UNTRUSTED, MessageProvenance.trustOf(history.child()).orElseThrow());
        assertEquals(ChatSourceKind.SUMMARY, MessageProvenance.sourceOf(history.summary()).orElseThrow());
        assertEquals(ChatTrustLevel.DERIVED, MessageProvenance.trustOf(history.summary()).orElseThrow());
        assertFalse(MessageProvenance.isTrustedInstruction(history.secondSummary()));
        assertTrue(MessageProvenance.provenanceOf(history.summary()).size() >= 2);
        assertFalse(MessageProvenance.provenanceOf(history.secondSummary()).isEmpty());
        assertEquals(ChatTrustLevel.UNTRUSTED,
                MessageProvenance.trustOf(ContextProvenance.clampExternalClaim()).orElseThrow());
        ContextProvenance.main(new String[0]);
    }
}
