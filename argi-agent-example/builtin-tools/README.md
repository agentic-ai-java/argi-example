# builtin-tools

Registers built-in filesystem, search, and todo tools with neutral bindings.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/builtin-tools
```

## Behavior

Creates `target/tool-workspace/notes.txt`, then gives the agent filesystem, glob, grep, and todo bindings scoped to that directory. The requested output is `summary.txt` in the same workspace.

## Source and verification

- [BuiltinToolsExample.java](src/main/java/io/github/agentic/ai/examples/BuiltinToolsExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Registers built-in filesystem, search, and todo tools with neutral bindings.

Limit: Filesystem, glob, grep and todo tools; not every upstream built-in tool is registered.
