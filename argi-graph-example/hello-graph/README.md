# hello-graph

A two-step model-free workflow that normalizes a name and creates a greeting.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/hello-graph
```

## Behavior

Expected result: `greeting=Welcome, Ada!`. The workflow has two ordinary Java nodes.

## Source and verification

- [HelloGraph.java](src/main/java/io/github/agentic/ai/examples/HelloGraph.java) is the primary entry point.
- [HelloGraphTest.java](src/test/java/io/github/agentic/ai/examples/HelloGraphTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- A two-step model-free workflow that normalizes a name and creates a greeting.

Limit: Introductory two-node graph only; it deliberately has no application service or model.
