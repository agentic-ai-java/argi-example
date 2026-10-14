# mcp-router

Exposes semantic MCP routing tools to a neutral ARGI agent.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Nacos plus the mcp-registry example, a model endpoint, and an embedding endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/mcp-router
```

## Behavior

The router embeds and searches service descriptions, then exposes route/proxy tools to the agent. It requires a real embedding model in addition to the chat model. The configured discovery source is Nacos.

## Configuration

- [application.yml](src/main/resources/application.yml): environment variable names and profile settings.

## Source and verification

- [McpRouterAgent.java](src/main/java/io/github/agentic/ai/examples/McpRouterAgent.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
