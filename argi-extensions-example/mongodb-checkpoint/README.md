# mongodb-checkpoint

Persists neutral graph state in the Extensions MongoDB checkpoint saver.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
An existing MongoDB service; set MONGODB_URI.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/mongodb-checkpoint
```

## Behavior

Uses a unique thread and reads the persisted checkpoint back through MongoSaver. It closes MongoClient on exit. Use a dedicated MongoDB environment and remove sample checkpoints through normal database administration.

## Source and verification

- [MongoCheckpoint.java](src/main/java/io/github/agentic/ai/examples/MongoCheckpoint.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Persists neutral graph state in the Extensions MongoDB checkpoint saver.
- Fresh saver and graph read pending checkpoints, apply one approval, inspect history and leave another thread pending.

Limit: Restoration uses fresh saver objects on one client; database restart, replica failover and concurrent writers remain unverified.

## Source layout

| Source | Responsibility |
| --- | --- |
| [MongoCheckpoint.java](src/main/java/io/github/agentic/ai/examples/MongoCheckpoint.java) | Uses an existing MongoDB service and a unique example thread. |
| [MongoRecovery.java](src/main/java/io/github/agentic/ai/examples/MongoRecovery.java) | Demonstrates checkpoint recovery through a fresh graph using the selected external saver. |
