# structured-output

Selects schema enforcement according to adapter capabilities and validates the parsed domain record.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/structured-output
```

## Behavior

Requests `category` and `priority`, then strictly parses and validates the `Ticket` record.
Adapters advertising neutral structured output receive `TicketSchema.format()`.
The pinned Spring AI adapter does not advertise that capability: it receives the
schema in the prompt and returns plain text, which is validated by the application.
That fallback is not provider-enforced JSON Schema. Missing/extra fields, incorrect
types and invalid domain values fail validation.

## Source and verification

- [StructuredOutput.java](src/main/java/io/github/agentic/ai/examples/StructuredOutput.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Selects schema enforcement according to adapter capabilities and validates the parsed domain record.
- Explicit response format and ReactAgent outputType; domain validation rejects missing/extra fields, wrong types and out-of-range priorities.

Limit: A ticket schema; provider schema adherence remains model-dependent, so application validation is mandatory.

## Source layout

| Source | Responsibility |
| --- | --- |
| [StructuredOutput.java](src/main/java/io/github/agentic/ai/examples/StructuredOutput.java) | Separates a provider's schema request from application-side result validation. |
| [TicketSchema.java](src/main/java/io/github/agentic/ai/examples/TicketSchema.java) | One schema is shared by the model request and strict application validation. |
| [TicketValidationScenarios.java](src/main/java/io/github/agentic/ai/examples/TicketValidationScenarios.java) | Shows that valid JSON alone does not make a valid domain result. |
| [TypedTicketAgent.java](src/main/java/io/github/agentic/ai/examples/TypedTicketAgent.java) | Demonstrates ReactAgent's outputType in addition to the lower-level response format. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### TicketValidationScenarios

Shows that valid JSON alone does not make a valid domain result. Runtime: **local**.

```shell
bash tools/run.sh argi-agent-example/structured-output -Dexample.mainClass=io.github.agentic.ai.examples.TicketValidationScenarios
```

### TypedTicketAgent

Demonstrates ReactAgent's outputType in addition to the lower-level response format. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/structured-output -Dexample.mainClass=io.github.agentic.ai.examples.TypedTicketAgent
```
