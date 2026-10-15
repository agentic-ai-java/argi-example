# a2a

Discovers a remote A2A agent card and delegates a task over the A2A protocol.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
An existing A2A service with a public agent card; set A2A_BASE_URL.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/a2a
```

## Behavior

The configured service must expose a public agent card. This sample does not configure authenticated extended cards. It logs the remote research result.

## Source and verification

- [A2aExample.java](src/main/java/io/github/agentic/ai/examples/A2aExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Discovers a remote A2A agent card and delegates a task over the A2A protocol.

Limit: Remote client delegation; no A2A server, authentication flow, task resubscription or transport failure matrix.
