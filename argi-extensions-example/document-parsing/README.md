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

- [DocumentParsing.java](src/main/java/io/github/agentic/ai/examples/DocumentParsing.java) contains the complete example.
- [DocumentParsingTest.java](src/test/java/io/github/agentic/ai/examples/DocumentParsingTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
