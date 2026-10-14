# hooks-and-retries

Combines PII redaction, model/tool budgets, and bounded retries for read-only tools.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/hooks-and-retries
```

## Behavior

Redacts email input, caps model and tool calls, and uses bounded retries for a read-only tool. Retry exceptions and budgets are visible in the builder; write-effect recovery needs a different policy.

## Source and verification

- [HooksAndRetries.java](src/main/java/io/github/agentic/ai/examples/HooksAndRetries.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
