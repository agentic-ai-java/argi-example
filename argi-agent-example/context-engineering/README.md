# context-engineering

Preserves per-thread conversation history and enforces model call budgets.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/context-engineering
```

## Behavior

Two turns share `customer-42`. A third model turn in that thread exceeds the configured budget; a different thread has its own budget.

## Source and verification

- [ContextEngineering.java](src/main/java/io/github/agentic/ai/examples/ContextEngineering.java) contains the complete example.
- [ContextEngineeringTest.java](src/test/java/io/github/agentic/ai/examples/ContextEngineeringTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
