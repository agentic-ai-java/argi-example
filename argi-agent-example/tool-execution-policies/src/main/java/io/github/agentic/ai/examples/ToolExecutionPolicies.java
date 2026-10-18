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

import io.github.agentic.ai.api.chat.ChatMessage;
import io.github.agentic.ai.api.chat.ToolCall;
import io.github.agentic.ai.api.tool.Tool;
import io.github.agentic.ai.api.tool.ToolContext;
import io.github.agentic.ai.api.chat.ToolDefinition;
import io.github.agentic.ai.api.tool.ToolRegistry;
import io.github.agentic.ai.api.tool.ToolResult;
import io.github.agentic.ai.graph.OverAllState;
import io.github.agentic.ai.graph.RunnableConfig;
import io.github.agentic.ai.graph.agent.interceptor.BusinessOutcomeMapperRegistry;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallHandler;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallRequest;
import io.github.agentic.ai.graph.agent.interceptor.ToolCallResponse;
import io.github.agentic.ai.graph.agent.interceptor.ToolInterceptor;
import io.github.agentic.ai.graph.agent.interceptor.authorization.AuthorizationDecision;
import io.github.agentic.ai.graph.agent.interceptor.authorization.ToolAuthorizationInterceptor;
import io.github.agentic.ai.graph.agent.node.AgentToolNode;
import io.github.agentic.ai.graph.execution.PrincipalReference;
import io.github.agentic.ai.graph.execution.TenantReference;
import io.github.agentic.ai.graph.execution.TrustedExecutionContext;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Exercises real tool dispatch with trusted identity, argument validation and domain outcomes. */
public final class ToolExecutionPolicies {
    private static final Logger LOG = LoggerFactory.getLogger(ToolExecutionPolicies.class);
    private static final String SCHEMA = """
            {"type":"object","required":["invoiceId"],"additionalProperties":false,
             "properties":{"invoiceId":{"$ref":"#/$defs/id"}},
             "$defs":{"id":{"type":"integer","minimum":1}}}
            """;

    private ToolExecutionPolicies() {
    }

    public record Result(ToolCallResponse response, int callbackCount) {
        @Override
        public String toString() {
            return "status=" + response.getStatus() + ", callbacks=" + callbackCount
                    + ", metadata=" + response.getMetadata();
        }
    }

    public static Result dispatch(String arguments, boolean authenticated, boolean permitted,
            boolean mapBusinessOutcome) throws Exception {
        final AtomicInteger calls = new AtomicInteger();
        final Tool invoice = new Tool() {
            @Override
            public ToolDefinition definition() {
                return new ToolDefinition("reviewInvoice", "Review an invoice in the local ledger.", SCHEMA);
            }

            @Override
            public CompletionStage<ToolResult> execute(ToolCall call, ToolContext context) {
                calls.incrementAndGet();
                // A completed transport can still carry a business rejection.
                return CompletableFuture.completedFuture(ToolResult.text("DECLINED"));
            }
        };
        final BusinessOutcomeMapperRegistry outcomes = mapBusinessOutcome
                ? BusinessOutcomeMapperRegistry.builder()
                    .declare("reviewInvoice", (call, result) -> new ToolResult(result.content(), true,
                            Map.of("errorCode", "INVOICE_DECLINED")))
                    .build()
                : BusinessOutcomeMapperRegistry.empty();
        final AtomicReference<ToolCallResponse> captured = new AtomicReference<>();
        final ToolInterceptor observe = new ToolInterceptor() {
            @Override
            public ToolCallResponse interceptToolCall(ToolCallRequest request, ToolCallHandler handler) {
                final ToolCallResponse response = handler.call(request);
                captured.set(response);
                return response;
            }

            @Override
            public String getName() {
                return "observe-local-result";
            }
        };
        final ToolAuthorizationInterceptor authorization = new ToolAuthorizationInterceptor((name, identity) ->
                permitted && "tenant-a".equals(identity.tenant().id())
                        ? AuthorizationDecision.allow()
                        : AuthorizationDecision.deny("Invoice access is not granted"));
        final AgentToolNode node = AgentToolNode.builder()
                .agentName("invoice-review")
                .toolRegistry(new ToolRegistry(List.of(invoice)))
                .validateToolArguments(true)
                .businessOutcomeMappers(outcomes)
                .parallelToolExecution(false)
                .toolExecutionTimeout(Duration.ofSeconds(5))
                .throwOnToolError(false)
                .build();
        node.setToolInterceptors(List.of(observe, authorization));
        final OverAllState state = new OverAllState(Map.of("messages",
                List.of(ChatMessage.assistant("", List.of(new ToolCall("review-1", "reviewInvoice", arguments))))));
        final var executor = Executors.newFixedThreadPool(2);
        try {
            final RunnableConfig.Builder config = RunnableConfig.builder()
                    .threadId("invoice-session")
                    .addParallelNodeExecutor("_AGENT_TOOL_", executor);
            if (authenticated) {
                // Local application fixture. Production identity must come from authenticated ingress.
                config.trustedExecutionContext(TrustedExecutionContext.builder()
                        .tenant(new TenantReference("example", "tenant-a"))
                        .principal(new PrincipalReference("example", "reviewer"))
                        .runId("invoice-run")
                        .correlationId("invoice-trace")
                        .build());
            }
            node.apply(state, config.build());
            return new Result(captured.get(), calls.get());
        }
        finally {
            executor.shutdownNow();
        }
    }

    public static void main(String[] args) throws Exception {
        LOG.info("Missing identity: {}", dispatch("{\"invoiceId\":1}", false, true, true));
        LOG.info("Revoked permission: {}", dispatch("{\"invoiceId\":1}", true, false, true));
        LOG.info("Invalid arguments: {}", dispatch("{\"invoiceId\":0}", true, true, true));
        LOG.info("Registered business rejection: {}", dispatch("{\"invoiceId\":1}", true, true, true));
        LOG.info("Unmapped payload: {}", dispatch("{\"invoiceId\":1}", true, true, false));
    }
}
