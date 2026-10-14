# spring-ai-integration

Adapts a Spring AI model into a neutral streaming LlmNode and consumes graph events.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/spring-ai-integration
```

## Behavior

Consumes token/node events once and commits the final model response into `answer`. Uses the native Spring AI adapter with the framework-neutral `LlmNode`.

## Source and verification

- [GraphModelStreaming.java](src/main/java/io/github/agentic/ai/examples/GraphModelStreaming.java) contains the complete example.
- [GraphModelStreamingTest.java](src/test/java/io/github/agentic/ai/examples/GraphModelStreamingTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
