# rag-retrieval

Expands a query with HyDE and retrieves evidence through the RAG graph node.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
A real chat and embedding endpoint; set ARGI_API_KEY, ARGI_MODEL, and optionally ARGI_EMBEDDING_MODEL.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/rag-retrieval
```

## Behavior

Calls a real chat model to generate a hypothetical passage, then searches a vector store through KnowledgeRetrievalNode. Documents are converted into id/text/metadata maps before entering RC2 graph state. ARGI_EMBEDDING_MODEL defaults to text-embedding-3-small.

## Source and verification

- [RagRetrieval.java](src/main/java/io/github/agentic/ai/examples/RagRetrieval.java) contains the complete example.
- [RagRetrievalTest.java](src/test/java/io/github/agentic/ai/examples/RagRetrievalTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
