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

- [A2aExample.java](src/main/java/io/github/agentic/ai/examples/A2aExample.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
