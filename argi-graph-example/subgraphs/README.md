# subgraphs

A StateGraph subgraph shares parent state and produces a normalized invoice.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/subgraphs
```

## Behavior

Expected invoice: `Ada: 120`. The child is a `StateGraph` compiled by the parent and uses shared state keys.

## Source and verification

- [Subgraphs.java](src/main/java/io/github/agentic/ai/examples/Subgraphs.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- A StateGraph subgraph shares parent state and produces a normalized invoice.

Limit: Shared parent/child state example; no distributed subgraph execution.
