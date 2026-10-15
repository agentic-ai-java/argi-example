# jdbc-chat-memory

Persists a bounded Spring AI conversation with the Extensions H2 repository.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/jdbc-chat-memory
```

## Behavior

Uses H2 with DATABASE_TO_LOWER=TRUE and an explicit schema. A new memory facade reads persisted messages; tests cover bounded windows, conversation isolation, and clearing.

## Source and verification

- [JdbcChatMemory.java](src/main/java/io/github/agentic/ai/examples/JdbcChatMemory.java) is the primary entry point.
- [JdbcChatMemoryTest.java](src/test/java/io/github/agentic/ai/examples/JdbcChatMemoryTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Persists a bounded Spring AI conversation with the Extensions H2 repository.
- H2-backed window eviction, reopening a facade, conversation isolation and deletion without affecting another conversation.

Limit: Local H2 message storage; no remote JDBC outage or load experiment.

## Source layout

| Source | Responsibility |
| --- | --- |
| [JdbcChatMemory.java](src/main/java/io/github/agentic/ai/examples/JdbcChatMemory.java) | Stores a bounded Spring AI conversation using the Extensions JDBC repository. |
| [JdbcMemoryLifecycle.java](src/main/java/io/github/agentic/ai/examples/JdbcMemoryLifecycle.java) | Demonstrates window eviction, reopening, isolation and deletion using local H2. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### JdbcMemoryLifecycle

Demonstrates window eviction, reopening, isolation and deletion using local H2. Runtime: **local**.

```shell
bash tools/run.sh argi-extensions-example/jdbc-chat-memory -Dexample.mainClass=io.github.agentic.ai.examples.JdbcMemoryLifecycle
```
