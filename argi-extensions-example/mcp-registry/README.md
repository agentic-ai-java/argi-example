# mcp-registry

Publishes a read-only MCP tool and registers the server in Nacos.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Nacos 3 with MCP registry support; this application serves and registers argi-catalog on port 8092.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/mcp-registry
```

## Behavior

Publishes the read-only lookup tool as service `argi-catalog` version 1.0.0. Starting this application registers that service with Nacos. Do not use a production registry for experimentation.

## Configuration

- [application.yml](src/main/resources/application.yml): environment variable names and profile settings.

## Source and verification

- [McpRegistryServer.java](src/main/java/io/github/agentic/ai/examples/McpRegistryServer.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Publishes a read-only MCP tool and registers the server in Nacos.

Limit: One annotated catalog tool registered in Nacos; no authenticated public server deployment.
