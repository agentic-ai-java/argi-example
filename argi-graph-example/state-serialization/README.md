# state-serialization

Append and replace reducers plus neutral message serialization round trips.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/state-serialization
```

## Behavior

The recovered events are `received`, `accepted`, `finished`; status is `done`. Messages deserialize as neutral ARGI `ChatMessage` values.

## Source and verification

- [StateSerialization.java](src/main/java/io/github/agentic/ai/examples/StateSerialization.java) is the primary entry point.
- [StateSerializationTest.java](src/test/java/io/github/agentic/ai/examples/StateSerializationTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Append and replace reducers plus neutral message serialization round trips.
- Complete assistant/tool exchange, metadata, binary image content, text editing that preserves attachments and checkpoint round-trip equality.

Limit: Neutral text, tool and image messages; live audio/video provider support is not demonstrated.

## Source layout

| Source | Responsibility |
| --- | --- |
| [MessageFixtures.java](src/main/java/io/github/agentic/ai/examples/MessageFixtures.java) | Neutral messages include content, metadata and stable tool invocation identifiers. |
| [MessageRoundTrip.java](src/main/java/io/github/agentic/ai/examples/MessageRoundTrip.java) | Round-trips binary content and tool messages without making a model request. |
| [StateSerialization.java](src/main/java/io/github/agentic/ai/examples/StateSerialization.java) | Keeps model messages and checkpoint state independent of provider SDK classes. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### MessageRoundTrip

Round-trips binary content and tool messages without making a model request. Runtime: **local**.

```shell
bash tools/run.sh argi-graph-example/state-serialization -Dexample.mainClass=io.github.agentic.ai.examples.MessageRoundTrip
```
