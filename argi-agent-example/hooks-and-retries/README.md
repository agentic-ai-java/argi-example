# hooks-and-retries

Combines PII redaction, model/tool budgets, and bounded retries for read-only tools.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/hooks-and-retries
```

## Behavior

Redacts email input, caps model and tool calls, and uses bounded retries for a read-only tool. Retry exceptions and budgets are visible in the builder; write-effect recovery needs a different policy.

## Source and verification

- [HooksAndRetries.java](src/main/java/io/github/agentic/ai/examples/HooksAndRetries.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Combines PII redaction, model/tool budgets, and bounded retries for read-only tools.
- An explicit fail-once read tool exercises retry rather than merely installing retry configuration.

Limit: Retry is limited to a read-only fixture. Non-idempotent tool recovery requires a separate design.

## Source layout

| Source | Responsibility |
| --- | --- |
| [FailOnceTrackingTool.java](src/main/java/io/github/agentic/ai/examples/FailOnceTrackingTool.java) | An explicit fault-injection fixture for a read-only lookup. |
| [HooksAndRetries.java](src/main/java/io/github/agentic/ai/examples/HooksAndRetries.java) | Retries apply to read-only lookup; write tools require effect-aware recovery instead. |
| [RetryScenarios.java](src/main/java/io/github/agentic/ai/examples/RetryScenarios.java) | Forces one transient tool failure so that the retry path is actually exercised. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### RetryScenarios

Forces one transient tool failure so that the retry path is actually exercised. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/hooks-and-retries -Dexample.mainClass=io.github.agentic.ai.examples.RetryScenarios
```
