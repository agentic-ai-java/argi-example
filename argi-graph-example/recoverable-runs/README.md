# Recoverable run and task storage

This module demonstrates the opt-in `RunStore` contract available in the pinned
Core main revision in [compatibility.md](../../docs/compatibility.md).
For exact definition versions and recovery admission, also see
[versioned-recovery](../versioned-recovery/README.md).

## Requirements

Java 17+, Maven 3.9.1+, and the matching Core artifact. No API key, database,
container or model is required. `MemoryRunStore` is the actual Core implementation
used by this example and does not provide process-restart durability.

## Run

From the repository root after installing matching dependencies:

```shell
bash tools/run.sh argi-graph-example/recoverable-runs
bash tools/run.sh argi-graph-example/recoverable-runs -Dexample.mainClass=io.github.agentic.ai.examples.RunConflictScenarios
```

Run these commands after installing the reactor. Both entry points passed local validation on 2026-10-15.

## Demonstrated behavior

1. Create an application-owned trusted context with tenant, principal, run identity,
   deadline and budget. The fixed identity is an explicit local fixture.
2. Serialize and restore a data-only `RunSpec`, then submit it with the live context.
3. Submit the same request with another run ID and receive the original admitted run.
4. Acquire a physical lease and atomically store a running run, checkpoint and pending task.
5. Move the run to `WAITING`, release ownership, acquire and renew a new lease, and
   reject writes from the old owner. The logical session remains reserved.
6. Resume and atomically finish the task and run with a checkpoint, approval journal
   entry and outbox event. The session reservation is released at completion.
7. Reject changed input for the same submission key, a competing owner, a stale
   revision and an invalid outbox entry. A rejected commit leaves its checkpoint unchanged.

The expected final run revision is 4 and the task revision is 2. The main program
prints the deduplication, fencing and session-release outcomes, the stored records,
and four rejected operations. Outbox records remain available for an application
dispatcher; this example does not send them anywhere.

## Source layout

| Source | Responsibility |
| --- | --- |
| [RecoverableRuns.java](src/main/java/io/github/agentic/ai/examples/RecoverableRuns.java) | Main entry point and observable results. |
| [RunExampleContext.java](src/main/java/io/github/agentic/ai/examples/RunExampleContext.java) | Application identity fixture and versioned run input. |
| [StoredRunWorkflow.java](src/main/java/io/github/agentic/ai/examples/StoredRunWorkflow.java) | Stored run/task lifecycle, lease handoff and atomic completion. |
| [RunConflictScenarios.java](src/main/java/io/github/agentic/ai/examples/RunConflictScenarios.java) | Conflict and failed-write atomicity scenarios. |
| [RecoverableRunsTest.java](src/test/java/io/github/agentic/ai/examples/RecoverableRunsTest.java) | Assertions for the lifecycle, conflicts, codec and durability declaration. |

## Boundaries and verification

This is storage-contract usage, not an autonomous task executor. No task code is
serialized or dispatched, no outbox delivery or exactly-once side effect is promised,
and the supplied memory implementation is not a durable backend. Authentication,
code registration and production recovery belong to the application.

Compilation, local tests and both entry points passed on 2026-10-15. See the
[validation record](../../docs/compatibility.md#verification-boundary) for coverage
and the distinction between in-memory storage contracts and durable infrastructure.
