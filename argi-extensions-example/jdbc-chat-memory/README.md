# jdbc-chat-memory

Persists a bounded Spring AI conversation with the Extensions H2 repository.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/jdbc-chat-memory
```

## Behavior

Uses H2 with DATABASE_TO_LOWER=TRUE and an explicit schema. A new memory facade reads persisted messages; tests cover bounded windows, conversation isolation, and clearing.

## Source and verification

- [JdbcChatMemory.java](src/main/java/io/github/agentic/ai/examples/JdbcChatMemory.java) contains the complete example.
- [JdbcChatMemoryTest.java](src/test/java/io/github/agentic/ai/examples/JdbcChatMemoryTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
