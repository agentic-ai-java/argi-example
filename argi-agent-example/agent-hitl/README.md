# agent-hitl

Pauses tool calls and accepts approved, rejected, or edited human feedback.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/agent-hitl -Dexec.args=REJECTED
```

## Behavior

The default decision is `REJECTED`. Pass `APPROVED` or `EDITED` explicitly; editing changes the requested order to ORDER-43. The example uses a read-only lookup to show the full approval protocol.

## Source and verification

- [AgentApproval.java](src/main/java/io/github/agentic/ai/examples/AgentApproval.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Pauses tool calls and accepts approved, rejected, or edited human feedback.

Limit: Read-only tracking tool approval; no real financial side effect or review UI.
