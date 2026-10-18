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

- [GraphModelStreaming.java](src/main/java/io/github/agentic/ai/examples/GraphModelStreaming.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Adapts a Spring AI model into a neutral streaming LlmNode and consumes graph events.

Limit: One streaming LlmNode; provider-specific event extensions and live cancellation are not validated.
