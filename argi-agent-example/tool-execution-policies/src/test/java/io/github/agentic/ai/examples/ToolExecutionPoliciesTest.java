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

import io.github.agentic.ai.graph.agent.action.ActionType;
import io.github.agentic.ai.graph.agent.interceptor.ToolOutcome;
import io.github.agentic.ai.graph.agent.interceptor.toolretry.ToolEffect;
import io.github.agentic.ai.graph.agent.interceptor.toolselection.ToolSelectionFailedException;
import io.github.agentic.ai.graph.agent.interceptor.toolselection.ToolSelectionFailurePolicy;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(20)
class ToolExecutionPoliciesTest {
    @Test
    void deniesMissingIdentityAndRevokedPermissionsBeforeCallback() throws Exception {
        for (boolean authenticated : List.of(false, true)) {
            final var result = ToolExecutionPolicies.dispatch("{\"invoiceId\":1}", authenticated, false, true);
            assertEquals(ToolOutcome.AUTH_DENIED, result.response().getStatus());
            assertEquals(0, result.callbackCount());
        }
    }

    @Test
    void invalidArgumentsNeverReachTheCallback() throws Exception {
        for (String arguments : List.of("{}", "{\"invoiceId\":0}", "{\"invoiceId\":\"secret-value\"}", "not-json")) {
            final var result = ToolExecutionPolicies.dispatch(arguments, true, true, true);
            assertEquals(ToolOutcome.ERROR, result.response().getStatus());
            assertEquals(0, result.callbackCount());
            assertTrue(result.response().getMetadata().containsKey("errorCode"));
            assertFalse(result.response().getResult().contains("secret-value"));
        }
    }

    @Test
    void businessOutcomeRequiresExplicitMapping() throws Exception {
        final var mapped = ToolExecutionPolicies.dispatch("{\"invoiceId\":1}", true, true, true);
        assertEquals(1, mapped.callbackCount());
        assertEquals(ToolOutcome.BUSINESS_FAILED, mapped.response().getStatus());
        assertTrue(mapped.response().isError());
        assertEquals(true, mapped.response().toMessage().metadata().get("tool.error"));
        final var unmapped = ToolExecutionPolicies.dispatch("{\"invoiceId\":1}", true, true, false);
        assertEquals(ToolOutcome.SUCCESS, unmapped.response().getStatus());
        assertFalse(unmapped.response().isError());
    }

    @Test
    void uncertainWritesAreNotRetriedLikeReads() {
        final var read = EffectAwareRetries.timeoutThenSuccess(ToolEffect.READ_ONLY);
        assertEquals(2, read.attempts());
        assertEquals(ToolOutcome.SUCCESS, read.response().getStatus());
        final var write = EffectAwareRetries.timeoutThenSuccess(ToolEffect.UNSAFE);
        assertEquals(1, write.attempts());
        assertTrue(write.response().isError());
        assertEquals(true, write.response().getMetadata().get("inDoubt"));
    }

    @Test
    void selectionFailureCanKeepAllKeepMandatoryOrStop() {
        final var client = SelectionFailurePolicies.unavailableClient();
        final var open = SelectionFailurePolicies.select(client, ToolSelectionFailurePolicy.FAIL_OPEN_ALL_TOOLS);
        assertEquals(List.of("help", "reviewInvoice", "chargeCard"), open.tools());
        assertNull(open.decision());
        final var closed = SelectionFailurePolicies.select(client, ToolSelectionFailurePolicy.FAIL_CLOSED_ALWAYS_INCLUDE);
        assertEquals(List.of("help"), closed.tools());
        assertEquals(ActionType.TOOL_CALL, closed.decision().type());
        final var stopped = assertThrows(ToolSelectionFailedException.class,
                () -> SelectionFailurePolicies.select(client, ToolSelectionFailurePolicy.FAIL_CLOSED_THROW));
        assertEquals(ActionType.STOP, stopped.getDecision().type());
    }

    @Test
    void localEntryPointsComplete() throws Exception {
        ToolExecutionPolicies.main(new String[0]);
        EffectAwareRetries.main(new String[0]);
        SelectionFailurePolicies.main(new String[0]);
    }
}
