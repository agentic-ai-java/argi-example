# jdbc-checkpoint

Restores a pending approval through an H2 JDBC checkpoint saver and a new graph.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/jdbc-checkpoint
```

## Behavior

Uses H2 in memory for the console run. The test uses a file-backed database and rebuilds both graph and saver before resuming, demonstrating actual JDBC persistence rather than sharing a MemorySaver.

## Source and verification

- [JdbcCheckpoint.java](src/main/java/io/github/agentic/ai/examples/JdbcCheckpoint.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Restores a pending approval through an H2 JDBC checkpoint saver and a new graph.

Limit: Local H2 recovery; no production JDBC migration, contention or failover experiment.
