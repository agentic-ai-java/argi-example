# mcp-gateway

Aggregates Nacos MCP services into independently routed SSE and Streamable endpoints.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Nacos plus the registered argi-catalog service. This application serves gateway endpoints on port 8093.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/mcp-gateway
```

## Behavior

Exposes `/catalog/mcp` (Streamable HTTP) and `/catalog/sse` plus `/catalog/message` (SSE). Both route to argi-catalog. Activate Spring profile `oauth` only with configured client credentials and token endpoints.

## Configuration

- [application-oauth.yml](src/main/resources/application-oauth.yml): environment variable names and profile settings.
- [application.yml](src/main/resources/application.yml): environment variable names and profile settings.

## Source and verification

- [McpGatewayExample.java](src/main/java/io/github/agentic/ai/examples/McpGatewayExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Aggregates Nacos MCP services into independently routed SSE and Streamable endpoints.

Limit: Starter-driven endpoint composition; no gateway authorization or routing E2E test.
