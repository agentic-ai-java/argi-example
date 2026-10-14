# plantuml

Exports PlantUML and Mermaid representations without invoking the graph.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/plantuml
```

## Behavior

Writes `target/diagrams/release.puml` and `release.mmd`. Open these with a PlantUML or Mermaid renderer of your choice. No image renderer or remote rendering service is started.

## Source and verification

- [GraphDiagrams.java](src/main/java/io/github/agentic/ai/examples/GraphDiagrams.java) contains the complete example.
- [GraphDiagramsTest.java](src/test/java/io/github/agentic/ai/examples/GraphDiagramsTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
