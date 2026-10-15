# routing-streaming

Uses LLM routing to choose a specialist and demonstrates neutral streamed messages.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/routing-streaming
```

## Behavior

A routing model selects the billing or technical specialist based on descriptions. Neutral messages are logged as the selected branch streams its answer.

## Source and verification

- [RoutingStreaming.java](src/main/java/io/github/agentic/ai/examples/RoutingStreaming.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Uses LLM routing to choose a specialist and demonstrates neutral streamed messages.

Limit: Model-selected specialists with a fallback; no routing accuracy evaluation or provider-event matrix.
