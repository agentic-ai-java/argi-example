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

- [MongoCheckpoint.java](src/main/java/io/github/agentic/ai/examples/MongoCheckpoint.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
