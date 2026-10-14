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

- [McpClientExample.java](src/main/java/io/github/agentic/ai/examples/McpClientExample.java) contains the complete example.
- [McpClientExampleTest.java](src/test/java/io/github/agentic/ai/examples/McpClientExampleTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
