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

- [RagRetrieval.java](src/main/java/io/github/agentic/ai/examples/RagRetrieval.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Expands a query with HyDE and retrieves evidence through the RAG graph node.

Limit: HyDE and graph retrieval; no reranker, hybrid retrieval, answer evaluation or ingestion lifecycle.
