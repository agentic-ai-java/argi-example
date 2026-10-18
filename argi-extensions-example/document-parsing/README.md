# document-parsing

Parses UTF-8 text and JSON records while preserving source metadata.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/document-parsing
```

## Behavior

Uses JSON Pointer `/articles`, selects title/body, and retains each record ID as source metadata. UTF-8 text remains intact. Blank text and missing JSON pointers fail explicitly.

## Source and verification

- [DocumentParsing.java](src/main/java/io/github/agentic/ai/examples/DocumentParsing.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Parses UTF-8 text and JSON records while preserving source metadata.
- Caller-selected JSON/text files, packaged input fixtures, source metadata projection and blank-document filtering.

Limit: JSON and UTF-8 text; PDF, Office, image OCR and upstream parser plugins are not included.

## Source layout

| Source | Responsibility |
| --- | --- |
| [DocumentIngestion.java](src/main/java/io/github/agentic/ai/examples/DocumentIngestion.java) | Reads caller-selected JSON or text and projects parsed documents into neutral records. |
| [DocumentParsing.java](src/main/java/io/github/agentic/ai/examples/DocumentParsing.java) | Parses JSON records and UTF-8 text before indexing documents. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### DocumentIngestion

Reads caller-selected JSON or text and projects parsed documents into neutral records. Runtime: **local**.

```shell
bash tools/run.sh argi-extensions-example/document-parsing -Dexample.mainClass=io.github.agentic.ai.examples.DocumentIngestion -Dexec.args=src/main/resources/articles.json
```
