# mcp-client

Initializes a Streamable HTTP MCP client and enumerates all tool pages.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
An existing Streamable HTTP MCP server; set MCP_BASE_URL and optionally MCP_ENDPOINT.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/mcp-client
```

## Behavior

Lists every tools/list page and closes the client. It does not invoke tools. For the supplied registry server, use MCP_BASE_URL=http://127.0.0.1:8092 and MCP_ENDPOINT=/mcp.

## Source and verification

- [McpClientExample.java](src/main/java/io/github/agentic/ai/examples/McpClientExample.java) is the primary entry point.
- [McpClientExampleTest.java](src/test/java/io/github/agentic/ai/examples/McpClientExampleTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Initializes a Streamable HTTP MCP client and enumerates all tool pages.
- Read-only invocation of the companion lookup tool, advertised-tool checks and protocol error handling.

Limit: Streamable HTTP discovery and the companion read-only tool; no OAuth, resource subscriptions or prompt APIs.

## Source layout

| Source | Responsibility |
| --- | --- |
| [CatalogToolCall.java](src/main/java/io/github/agentic/ai/examples/CatalogToolCall.java) | Calls only the read-only lookup tool supplied by the companion registry example. |
| [McpClientExample.java](src/main/java/io/github/agentic/ai/examples/McpClientExample.java) | Uses the Extensions HTTP transport to initialize MCP and enumerate all tool pages. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### CatalogToolCall

Calls only the read-only lookup tool supplied by the companion registry example. Runtime: **service**.

```shell
bash tools/run.sh argi-extensions-example/mcp-client -Dexample.mainClass=io.github.agentic.ai.examples.CatalogToolCall
```
