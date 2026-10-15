# scheduling

Schedules a graph at a fixed delay and shuts down the task manager after completion.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/scheduling
```

## Behavior

Runs two heartbeats with the selected delay, rate or cron policy (fixed delay by default), then stops the task and shuts down its manager. A ten-second bound prevents an indefinitely running example.

## Source and verification

- [ScheduledWorkflow.java](src/main/java/io/github/agentic/ai/examples/ScheduledWorkflow.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Schedules a graph at a fixed delay and shuts down the task manager after completion.
- Caller-selectable fixed-delay, fixed-rate and cron modes with bounded waiting and scheduler shutdown.

Limit: Single-process schedules; no distributed lock, misfire recovery or clustered scheduler.

## Source layout

| Source | Responsibility |
| --- | --- |
| [SchedulePolicies.java](src/main/java/io/github/agentic/ai/examples/SchedulePolicies.java) | Chooses one schedule mode explicitly; all modes use a bounded demonstration runner. |
| [ScheduledWorkflow.java](src/main/java/io/github/agentic/ai/examples/ScheduledWorkflow.java) | A bounded heartbeat demonstration with explicit scheduler cleanup. |

Pass `-Dexec.args=delay`, `-Dexec.args=rate`, or `-Dexec.args=cron` to select the schedule. Each runner waits for two callbacks and then stops the task and manager.
