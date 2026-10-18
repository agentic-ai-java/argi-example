# Batched array loops

Drives the actual LoopMode.array initialization and dispatch strategy with ordered JSON input.

## Requirements

Java 17+, Maven 3.9.1+, and Core revision
`5ec935b384d5379324ee60277ddcd3a6683a861f` built as `2.1.0-RC2-SNAPSHOT`.
No model key, container, database or network service is required.
Follow [source compatibility](../../docs/compatibility.md) to install dependencies.

## Run

From the repository root after dependency bootstrap and reactor installation:

```shell
bash tools/run.sh argi-agent-example/batched-array-loop
```

## Expected behavior

[1,2,3,4,5] with batch size 2 yields [1,2], [3,4], [5]. Explicit size 1 keeps array framing; the legacy constructor yields scalar text.

## Verification

```shell
bash tools/mvn.sh -B -pl :example-batched-array-loop -am verify
```

The tests assert the demonstrated behavior and boundary cases. See
[compatibility.md](../../docs/compatibility.md) for the actual validation record;
commands listed here are instructions, not a claim that execution occurred.

## Limits

This inspects dispatch without a model-backed child. Batching does not enable parallel execution or establish throughput improvements.

See the [website guide](https://github.com/agentic-ai-java/argi-website/blob/main/docs/frameworks/agent-framework/batched-array-loops.md).
