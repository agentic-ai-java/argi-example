# tair-vector-store

Writes real embeddings to a Tair Vector index and searches for relevant documents.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
A Tair Vector-capable service (ordinary Redis is insufficient), real embeddings, TAIR_URI, and matching ARGI_EMBEDDING_DIMENSIONS.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/tair-vector-store
```

## Behavior

Uses the dedicated `argi_example_knowledge` index and stable document ID `checkpoint-guide`. The configured vector dimension must match the embedding model. The current Tair store does not support VectorStore.delete; retain or remove the index using Tair administration tools.

## Source and verification

- [TairVectorStoreExample.java](src/main/java/io/github/agentic/ai/examples/TairVectorStoreExample.java) contains the complete example.
- [TairVectorStoreExampleTest.java](src/test/java/io/github/agentic/ai/examples/TairVectorStoreExampleTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
