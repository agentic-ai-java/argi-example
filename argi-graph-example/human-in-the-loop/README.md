# human-in-the-loop

Pauses before a refund decision and resumes from the same checkpoint.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/human-in-the-loop
```

## Behavior

The saved state first contains `awaiting-approval`. Explicit feedback changes the final result to `refunded` or `rejected`.

## Source and verification

- [HumanInTheLoop.java](src/main/java/io/github/agentic/ai/examples/HumanInTheLoop.java) is the primary entry point.
- [HumanInTheLoopTest.java](src/test/java/io/github/agentic/ai/examples/HumanInTheLoopTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Pauses before a refund decision and resumes from the same checkpoint.
- Two independently paused requests receive different approval decisions before resuming.

Limit: Approval changes example state; it does not issue a real refund.

## Source layout

| Source | Responsibility |
| --- | --- |
| [ApprovalScenarios.java](src/main/java/io/github/agentic/ai/examples/ApprovalScenarios.java) | Resolves two independent pending requests with different human decisions. |
| [HumanInTheLoop.java](src/main/java/io/github/agentic/ai/examples/HumanInTheLoop.java) | Pauses before a refund decision and resumes from the same saved thread. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### ApprovalScenarios

Resolves two independent pending requests with different human decisions. Runtime: **local**.

```shell
bash tools/run.sh argi-graph-example/human-in-the-loop -Dexample.mainClass=io.github.agentic.ai.examples.ApprovalScenarios
```
