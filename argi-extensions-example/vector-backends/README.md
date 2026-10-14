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

The runner writes a stable example document with metadata, embeds a question, and retrieves matching documents. Provision the selected service and compatible schema/index first. The OpenSearch integration targets Alibaba Cloud OpenSearch, not the similarly named OpenSearch community cluster. Tair requires vector commands. Dimensions must match the embedding endpoint. Pass `--filter` only for a backend that implements metadata filters; it demonstrates `topic == 'checkpoint'`. The sample record is retained because deletion is not supported uniformly across backends.

## Configuration

- [application-analyticdb.yml](src/main/resources/application-analyticdb.yml): environment variable names and profile settings.
- [application-oceanbase.yml](src/main/resources/application-oceanbase.yml): environment variable names and profile settings.
- [application-opensearch.yml](src/main/resources/application-opensearch.yml): environment variable names and profile settings.
- [application-tablestore.yml](src/main/resources/application-tablestore.yml): environment variable names and profile settings.
- [application-tair.yml](src/main/resources/application-tair.yml): environment variable names and profile settings.

## Source and verification

- [VectorBackends.java](src/main/java/io/github/agentic/ai/examples/VectorBackends.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
