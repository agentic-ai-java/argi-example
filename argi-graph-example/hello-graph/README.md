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

- [HelloGraph.java](src/main/java/io/github/agentic/ai/examples/HelloGraph.java) contains the complete example.
- [HelloGraphTest.java](src/test/java/io/github/agentic/ai/examples/HelloGraphTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
