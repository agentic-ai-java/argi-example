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

- [RedisCheckpoint.java](src/main/java/io/github/agentic/ai/examples/RedisCheckpoint.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
