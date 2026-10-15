# vector-backends

Profile-selected AnalyticDB, OceanBase, OpenSearch, TableStore, and Tair indexing and search.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
The selected vector service, a real embedding endpoint, and one Maven/Spring profile pair.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/vector-backends -Pvector-tair -Dexec.args=--spring.profiles.active=tair
```

## Behavior

Profile-selected AnalyticDB, OceanBase, OpenSearch, TableStore, and Tair indexing and search.

## Backend profiles

Select exactly one Maven profile and the matching Spring profile:

| Backend | Maven profile | Spring profile | Configuration |
| --- | --- | --- | --- |
| analyticdb | `vector-analyticdb` | `analyticdb` | [application-analyticdb.yml](src/main/resources/application-analyticdb.yml) |
| oceanbase | `vector-oceanbase` | `oceanbase` | [application-oceanbase.yml](src/main/resources/application-oceanbase.yml) |
| opensearch | `vector-opensearch` | `opensearch` | [application-opensearch.yml](src/main/resources/application-opensearch.yml) |
| tablestore | `vector-tablestore` | `tablestore` | [application-tablestore.yml](src/main/resources/application-tablestore.yml) |
| tair | `vector-tair` | `tair` | [application-tair.yml](src/main/resources/application-tair.yml) |

The runner writes three documents with stable IDs and metadata, embeds a question, and retrieves matching documents. Provision the selected service and compatible schema/index first. The OpenSearch integration targets Alibaba Cloud OpenSearch, not the similarly named OpenSearch community cluster. Tair requires vector commands. Dimensions must match the embedding endpoint. Pass `--filter` only for a backend that implements metadata filters; it demonstrates `source == 'argi-examples' && topic == 'checkpoint'`. The sample records are retained by default because deletion is not supported uniformly across backends.

## Configuration

- [application-analyticdb.yml](src/main/resources/application-analyticdb.yml): environment variable names and profile settings.
- [application-oceanbase.yml](src/main/resources/application-oceanbase.yml): environment variable names and profile settings.
- [application-opensearch.yml](src/main/resources/application-opensearch.yml): environment variable names and profile settings.
- [application-tablestore.yml](src/main/resources/application-tablestore.yml): environment variable names and profile settings.
- [application-tair.yml](src/main/resources/application-tair.yml): environment variable names and profile settings.

## Source and verification

- [VectorBackends.java](src/main/java/io/github/agentic/ai/examples/VectorBackends.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Profile-selected AnalyticDB, OceanBase, OpenSearch, TableStore, and Tair indexing and search.
- Three labelled documents, neutral search result projection, optional metadata filtering and opt-in deletion of example IDs.

Limit: Backend support for filters, deletion, upsert and index refresh differs. Optional operations require a compatible backend.

## Source layout

| Source | Responsibility |
| --- | --- |
| [KnowledgeCorpus.java](src/main/java/io/github/agentic/ai/examples/KnowledgeCorpus.java) | Stable document IDs and metadata make unfiltered and filtered searches comparable. |
| [KnowledgeSearch.java](src/main/java/io/github/agentic/ai/examples/KnowledgeSearch.java) | Keeps retrieval policy and provider documents outside an application's graph state. |
| [VectorBackends.java](src/main/java/io/github/agentic/ai/examples/VectorBackends.java) | Uses a real embedding provider and the selected Extensions VectorStore starter. |

The runner now indexes three labelled documents. Add `--delete-indexed` to the Spring arguments only when your backend supports deletion by ID; otherwise the example IDs are retained. Search visibility may depend on backend refresh behavior.
