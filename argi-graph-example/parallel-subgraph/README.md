# parallel-subgraph

Joins order checks and creates a receipt in a nested graph.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/parallel-subgraph
```

## Behavior

Expected receipt: `reserved/authorized`. For explicit concurrent branch scheduling, see `parallel-nodes`.

## Source and verification

- [ParallelSubgraph.java](src/main/java/io/github/agentic/ai/examples/ParallelSubgraph.java) contains the complete example.
- [ParallelSubgraphTest.java](src/test/java/io/github/agentic/ai/examples/ParallelSubgraphTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
