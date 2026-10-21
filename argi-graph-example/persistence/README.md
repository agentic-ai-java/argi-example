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

- [Persistence.java](src/main/java/io/github/agentic/ai/examples/Persistence.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- File checkpoints, thread identity, history inspection, and recovery with a fresh graph.
- Checkpoint history projection, fresh-graph recovery, editing pending state and keeping another thread paused.

Limit: Fresh graph and saver objects demonstrate restoration; OS process crashes and concurrent writers are not validated.

## Source layout

| Source | Responsibility |
| --- | --- |
| [CheckpointInspector.java](src/main/java/io/github/agentic/ai/examples/CheckpointInspector.java) | Presents checkpoint cursors without assuming a saver's history ordering. |
| [Persistence.java](src/main/java/io/github/agentic/ai/examples/Persistence.java) | Run with 'prepare' and then 'resume' to demonstrate process-independent recovery. |
| [RecoveryScenarios.java](src/main/java/io/github/agentic/ai/examples/RecoveryScenarios.java) | Reopens the saver, edits a suspended state and proves that another thread stays paused. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### RecoveryScenarios

Reopens the saver, edits a suspended state and proves that another thread stays paused. Runtime: **local**.

```shell
bash tools/run.sh argi-graph-example/persistence -Dexample.mainClass=io.github.agentic.ai.examples.RecoveryScenarios
```
