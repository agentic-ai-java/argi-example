# persistence

File checkpoints, thread identity, history inspection, and recovery with a fresh graph.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/persistence -Dexec.args=prepare
```

In a separate process, resume the saved thread:

```shell
bash tools/run.sh argi-graph-example/persistence -Dexec.args=resume
```

## Behavior

Run `prepare` first, then `resume` in a separate process. Files remain under this module's `target/checkpoints`. The same `invoice-42` thread resumes at the paused node. Removing that directory discards these sample checkpoints.

## Source and verification

- [Persistence.java](src/main/java/io/github/agentic/ai/examples/Persistence.java) contains the complete example.
- [PersistenceTest.java](src/test/java/io/github/agentic/ai/examples/PersistenceTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
