# interceptors

Custom model and tool interceptors measure duration and cache a read-only lookup.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/interceptors
```

## Behavior

The timing interceptor wraps model calls. The lookup cache is intentionally limited to the public, read-only tracking fixture and rebuilds each tool response with the current call ID. Do not reuse this cache for private multi-tenant data without scoping it.

## Source and verification

- [InterceptorsExample.java](src/main/java/io/github/agentic/ai/examples/InterceptorsExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Custom model and tool interceptors measure duration and cache a read-only lookup.
- Primary/fallback model configuration; outbound context editing; tool selection; explicitly synthetic tool emulation; assistant-message sanitization and tool-exception conversion.

Limit: Emulation is synthetic; fallback occurs only after a primary failure. RC2 ToolError returns diagnostic text without setting an error status.

## Source layout

| Source | Responsibility |
| --- | --- |
| [ContextEditingScenarios.java](src/main/java/io/github/agentic/ai/examples/ContextEditingScenarios.java) | Shows outbound tool-result compaction independently of provider response variability. |
| [FallbackAgent.java](src/main/java/io/github/agentic/ai/examples/FallbackAgent.java) | Configures a separate fallback model while preserving the neutral request pipeline. |
| [InterceptorsExample.java](src/main/java/io/github/agentic/ai/examples/InterceptorsExample.java) | Caches only the public tracking fixture; invocation IDs are rebuilt for each caller. |
| [MessageSanitizerScenarios.java](src/main/java/io/github/agentic/ai/examples/MessageSanitizerScenarios.java) | Inspects empty assistant-content normalization and model-visible tool exception text. |
| [ShippingQuoteTool.java](src/main/java/io/github/agentic/ai/examples/ShippingQuoteTool.java) | A fixed sample tariff gives tool selection a distinct alternative to order tracking. |
| [ToolPolicyScenarios.java](src/main/java/io/github/agentic/ai/examples/ToolPolicyScenarios.java) | Compares model-selected tools with explicitly requested tool emulation. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### ContextEditingScenarios

Shows outbound tool-result compaction independently of provider response variability. Runtime: **local**.

```shell
bash tools/run.sh argi-agent-example/interceptors -Dexample.mainClass=io.github.agentic.ai.examples.ContextEditingScenarios
```

### FallbackAgent

Configures a separate fallback model while preserving the neutral request pipeline. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/interceptors -Dexample.mainClass=io.github.agentic.ai.examples.FallbackAgent
```

Set `ARGI_FALLBACK_MODEL`; optionally set `ARGI_FALLBACK_API_KEY` and `ARGI_FALLBACK_BASE_URL`. A successful primary request does not trigger fallback.

### MessageSanitizerScenarios

Inspects empty assistant-content normalization and model-visible tool exception text. Runtime: **local**.

```shell
bash tools/run.sh argi-agent-example/interceptors -Dexample.mainClass=io.github.agentic.ai.examples.MessageSanitizerScenarios
```

### ToolPolicyScenarios

Compares model-selected tools with explicitly requested tool emulation. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/interceptors -Dexample.mainClass=io.github.agentic.ai.examples.ToolPolicyScenarios
```

The default is `select`. Pass `-Dexec.args=emulate` only to demonstrate synthetic model-generated tool output; emulated output is not a real quote. Selection and emulation can make extra model calls beyond the agent-turn budget.
