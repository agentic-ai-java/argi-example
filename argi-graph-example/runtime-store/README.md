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

- [RuntimeStore.java](src/main/java/io/github/agentic/ai/examples/RuntimeStore.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Separates transient invocation metadata, thread state, and long-lived namespaced memory.
- Namespace isolation, upsert, filtered/sorted pagination, targeted deletion and missing-item behavior.

Limit: MemoryStore demonstrates contracts; it is not durable storage or an authorization boundary.

## Source layout

| Source | Responsibility |
| --- | --- |
| [PreferenceRepository.java](src/main/java/io/github/agentic/ai/examples/PreferenceRepository.java) | Keeps namespace construction outside graph nodes and invocation metadata. |
| [RuntimeStore.java](src/main/java/io/github/agentic/ai/examples/RuntimeStore.java) | Reads a user preference shared by different graph threads through the Store contract. |
| [StoreLifecycle.java](src/main/java/io/github/agentic/ai/examples/StoreLifecycle.java) | Demonstrates upsert, namespace isolation, filtered pagination and targeted deletion. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### StoreLifecycle

Demonstrates upsert, namespace isolation, filtered pagination and targeted deletion. Runtime: **local**.

```shell
bash tools/run.sh argi-graph-example/runtime-store -Dexample.mainClass=io.github.agentic.ai.examples.StoreLifecycle
```
