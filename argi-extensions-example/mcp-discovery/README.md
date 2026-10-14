# mcp-discovery

Discovers registered MCP tools through Nacos and adapts callbacks for ReAct.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Nacos plus the mcp-registry example already running, and model credentials.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/mcp-discovery
```

## Behavior

Discovers argi-catalog through the Nacos Streamable configuration and converts Spring AI callbacks to neutral tools. Start the registry companion separately before running this client.

## Configuration

- [application.yml](src/main/resources/application.yml): environment variable names and profile settings.

## Source and verification

- [McpDiscoveryAgent.java](src/main/java/io/github/agentic/ai/examples/McpDiscoveryAgent.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
