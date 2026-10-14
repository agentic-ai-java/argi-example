# runtime-store

Separates transient invocation metadata, thread state, and long-lived namespaced memory.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/runtime-store
```

## Behavior

Both `chat-1` and `chat-2` read the same user preference from `MemoryStore`. Invocation metadata identifies the user; it is not sent as model invocation attributes.

## Source and verification

- [RuntimeStore.java](src/main/java/io/github/agentic/ai/examples/RuntimeStore.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
