# redis-checkpoint

Stores and reloads neutral graph checkpoints with the Extensions Redis saver.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
An existing Redis service; set REDIS_URL and optional REDIS_PASSWORD.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/redis-checkpoint
```

## Behavior

Uses a unique thread and one-hour checkpoint TTL. The Redisson client is closed after reading the saved checkpoint. No Redis service is started.

## Source and verification

- [RedisCheckpoint.java](src/main/java/io/github/agentic/ai/examples/RedisCheckpoint.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Stores and reloads neutral graph checkpoints with the Extensions Redis saver.
- Fresh saver and graph read pending checkpoints, apply one approval, inspect history and leave another thread pending.

Limit: Restoration uses fresh saver objects on one client; Redis restart, TTL expiry and cluster failover remain unverified.

## Source layout

| Source | Responsibility |
| --- | --- |
| [RedisCheckpoint.java](src/main/java/io/github/agentic/ai/examples/RedisCheckpoint.java) | Uses a namespaced example thread and an expiring Redis checkpoint. |
| [RedisRecovery.java](src/main/java/io/github/agentic/ai/examples/RedisRecovery.java) | Demonstrates checkpoint recovery through a fresh graph using the selected external saver. |
