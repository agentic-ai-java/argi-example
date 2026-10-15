# agentic-rag

Makes retrieval an agent tool and adds evidence through a MessagesModelHook.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/agentic-rag
```

## Behavior

The local keyword corpus has checkpoint, approval, and tool entries. A MessagesModelHook can preload evidence; the `search_docs` tool supports additional retrieval during ReAct. This example teaches orchestration; use the vector/RAG modules for semantic indexing.

## Source and verification

- [AgenticRag.java](src/main/java/io/github/agentic/ai/examples/AgenticRag.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Makes retrieval an agent tool and adds evidence through a MessagesModelHook.
- Stable source labels, deterministic local retrieval and no-match behavior; retrieval tools and evidence injection have separate implementations.

Limit: Small keyword corpus; no production ranking benchmark, vector retrieval or citation correctness guarantee.

## Source layout

| Source | Responsibility |
| --- | --- |
| [AgenticRag.java](src/main/java/io/github/agentic/ai/examples/AgenticRag.java) | A local documentation corpus makes the retrieval strategy explicit and reproducible. |
| [DocumentationCorpus.java](src/main/java/io/github/agentic/ai/examples/DocumentationCorpus.java) | A small immutable corpus with stable citation IDs and explicit no-match behavior. |
| [DocumentationSearchTool.java](src/main/java/io/github/agentic/ai/examples/DocumentationSearchTool.java) | Makes retrieval callable by the agent without sending the full corpus in every prompt. |
| [EvidenceHook.java](src/main/java/io/github/agentic/ai/examples/EvidenceHook.java) | Adds a source block for the latest user turn while preserving the original conversation. |
| [RetrievalScenarios.java](src/main/java/io/github/agentic/ai/examples/RetrievalScenarios.java) | Makes source selection and the no-evidence path inspectable without a model. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### RetrievalScenarios

Makes source selection and the no-evidence path inspectable without a model. Runtime: **local**.

```shell
bash tools/run.sh argi-agent-example/agentic-rag -Dexample.mainClass=io.github.agentic.ai.examples.RetrievalScenarios
```
