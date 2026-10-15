# mem0-memory

Writes semantic memories to Mem0 and retrieves them with an explicit user scope.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
An existing compatible Mem0 server and model credentials. The startup configures the Mem0 server.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/mem0-memory
```

## Behavior

Stores a preference for argi-example-user and uses Mem0ServerRequest.SearchRequest.mem0Builder with that user scope. The server may extract durable semantic memories. Use a dedicated Mem0 environment.

## Configuration

- [application.yml](src/main/resources/application.yml): environment variable names and profile settings.

## Source and verification

- [Mem0MemoryExample.java](src/main/java/io/github/agentic/ai/examples/Mem0MemoryExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Writes semantic memories to Mem0 and retrieves them with an explicit user scope.

Limit: Semantic write/search for one example user; no deletion, reconciliation or extraction-quality evaluation.
