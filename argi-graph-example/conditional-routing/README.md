# conditional-routing

Routes support tickets into urgent and normal queues using conditional edges.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/conditional-routing
```

## Behavior

Severity 4 or 5 selects `on-call`; lower severity selects `business-hours`. The test covers both branches.

## Source and verification

- [ConditionalRouting.java](src/main/java/io/github/agentic/ai/examples/ConditionalRouting.java) is the primary entry point.
- [ConditionalRoutingTest.java](src/test/java/io/github/agentic/ai/examples/ConditionalRoutingTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Routes support tickets into urgent and normal queues using conditional edges.
- Both route outcomes and the severity threshold are exercised by a dedicated scenario.

Limit: Deterministic severity routing; model-based routing lives in routing-streaming.

## Source layout

| Source | Responsibility |
| --- | --- |
| [ConditionalRouting.java](src/main/java/io/github/agentic/ai/examples/ConditionalRouting.java) | Routes support tickets without asking a model to make a deterministic decision. |
| [RoutingScenarios.java](src/main/java/io/github/agentic/ai/examples/RoutingScenarios.java) | Compares both sides of the severity boundary using the same compiled graph. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### RoutingScenarios

Compares both sides of the severity boundary using the same compiled graph. Runtime: **local**.

```shell
bash tools/run.sh argi-graph-example/conditional-routing -Dexample.mainClass=io.github.agentic.ai.examples.RoutingScenarios
```
