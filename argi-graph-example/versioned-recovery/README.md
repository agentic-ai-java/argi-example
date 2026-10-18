# Versioned recovery admission

Serializes exact graph, tool catalog, codec and application references and resolves them against an upgraded registry.

## Requirements

Java 17+, Maven 3.9.1+, and Core revision
`5ec935b384d5379324ee60277ddcd3a6683a861f` built as `2.1.0-RC2-SNAPSHOT`.
No model key, container, database or network service is required.
Follow [source compatibility](../../docs/compatibility.md) to install dependencies.

## Run

From the repository root after dependency bootstrap and reactor installation:

```shell
bash tools/run.sh argi-graph-example/versioned-recovery
```

## Expected behavior

A version-one run resolves version one even when version two exists; missing old versions and incompatible codecs are rejected. MemoryRunStore cannot enter durable mode.

## Verification

```shell
bash tools/mvn.sh -B -pl :example-versioned-recovery -am verify
```

The tests assert the demonstrated behavior and boundary cases. See
[compatibility.md](../../docs/compatibility.md) for the actual validation record;
commands listed here are instructions, not a claim that execution occurred.

## Limits

Registry values are local data definitions. This is an admission contract, not a task runner, persistent backend or cross-JVM recovery demonstration.

See the [website guide](https://github.com/agentic-ai-java/argi-website/blob/main/docs/frameworks/graph-core/versioned-recovery.md).
