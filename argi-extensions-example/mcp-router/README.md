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

- [McpRouterAgent.java](src/main/java/io/github/agentic/ai/examples/McpRouterAgent.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Exposes semantic MCP routing tools to a neutral ARGI agent.

Limit: Semantic MCP routing; no routing-quality evaluation or service-outage matrix.
