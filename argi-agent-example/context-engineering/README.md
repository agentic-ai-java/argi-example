# context-engineering

Preserves per-thread conversation history and enforces model call budgets.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/context-engineering
```

## Behavior

Two turns share `customer-42`. A third model turn in that thread exceeds the configured budget; a different thread has its own budget.

## Source and verification

- [ContextEngineering.java](src/main/java/io/github/agentic/ai/examples/ContextEngineering.java) is the primary entry point.
- [ContextEngineeringTest.java](src/test/java/io/github/agentic/ai/examples/ContextEngineeringTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Preserves per-thread conversation history and enforces model call budgets.
- Interleaved customer threads demonstrate isolation and per-thread budget exhaustion.

Limit: In-memory checkpoints and model-call limits; no cross-process session durability.

## Source layout

| Source | Responsibility |
| --- | --- |
| [ContextEngineering.java](src/main/java/io/github/agentic/ai/examples/ContextEngineering.java) | Maintains per-thread history and limits the total number of model turns. |
| [ConversationSessions.java](src/main/java/io/github/agentic/ai/examples/ConversationSessions.java) | Reuses one agent while keeping each customer's checkpoint history separate. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### ConversationSessions

Reuses one agent while keeping each customer's checkpoint history separate. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/context-engineering -Dexample.mainClass=io.github.agentic.ai.examples.ConversationSessions
```
