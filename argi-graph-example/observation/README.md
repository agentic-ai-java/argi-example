# observation

Records graph observations with a local Micrometer handler and no telemetry backend.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/observation
```

## Behavior

Logs local observation names and a completed-observation count. No prompt bodies, Prometheus endpoint, or OpenTelemetry deployment are added.

## Source and verification

- [LocalObservation.java](src/main/java/io/github/agentic/ai/examples/LocalObservation.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Records graph observations with a local Micrometer handler and no telemetry backend.

Limit: Successful local observation lifecycle; no exporter deployment or error-path observability matrix.
