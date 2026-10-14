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

Runs two heartbeats at a one-second fixed delay, then stops the task and shuts down its manager. A ten-second bound prevents an indefinitely running example.

## Source and verification

- [ScheduledWorkflow.java](src/main/java/io/github/agentic/ai/examples/ScheduledWorkflow.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
