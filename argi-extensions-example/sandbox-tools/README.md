# sandbox-tools

Adapts an Extensions sandbox Python tool into a neutral ReAct agent.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
An existing AgentScope Runtime sandbox manager; set SANDBOX_MANAGER_URL, optional SANDBOX_MANAGER_TOKEN, and model credentials.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/sandbox-tools
```

## Behavior

Requires an existing AgentScope Runtime manager. Running the sample requests a sandbox session, adapts the Python tool, and asks the model to calculate 42. The sandbox and manager are closed with try-with-resources. No local daemon is started by repository tooling.

## Source and verification

- [SandboxToolsExample.java](src/main/java/io/github/agentic/ai/examples/SandboxToolsExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Adapts an Extensions sandbox Python tool into a neutral ReAct agent.

Limit: Python sandbox tool integration; no sandbox provisioning or full browser/desktop tool catalog.
