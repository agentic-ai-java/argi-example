# Bounded background tasks

Uses one execution slot and one waiting slot to demonstrate capacity and duplicate-ID rejection, completion and explicit result removal.

## Requirements

Java 17+, Maven 3.9.1+, and Core revision
`5ec935b384d5379324ee60277ddcd3a6683a861f` built as `2.1.0-RC2-SNAPSHOT`.
No model key, container, database or network service is required.
Follow [source compatibility](../../docs/compatibility.md) to install dependencies.

## Run

From the repository root after dependency bootstrap and reactor installation:

```shell
bash tools/run.sh argi-agent-example/background-tasks
```

## Expected behavior

The third submission and a duplicate ID are rejected; both accepted tasks complete after the gate opens; owned resources close.

## Build and inspect

```shell
bash tools/mvn.sh -B -pl :example-background-tasks -am -Dmaven.test.skip=true package
```

Run the entry point above and compare its output with the expected behavior.
These teaching examples do not add a separate test suite or coverage requirement.
See [compatibility.md](../../docs/compatibility.md) for the actual validation record.

## Limits

This is a local JVM repository, without tenant isolation or crash recovery. Results may expire. Cancelling or hiding active work does not immediately free its slot.

See the [website guide](https://github.com/agentic-ai-java/argi-website/blob/main/docs/frameworks/agent-framework/background-tasks.md).
