# agent-as-tool

Registers a specialist as a neutral Tool, letting a coordinator delegate work.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/agent-as-tool
```

## Behavior

The coordinator exposes `java_reviewer` as a neutral tool. Child execution and invocation context are handled by `AgentTool`, not by a native SDK tool loop.

## Source and verification

- [AgentAsToolExample.java](src/main/java/io/github/agentic/ai/examples/AgentAsToolExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Registers a specialist as a neutral Tool, letting a coordinator delegate work.
- Typed specialist input, generated tool schema and explicit child context/reasoning boundaries.

Limit: In-process delegation; no distributed authorization or remote agent lifecycle.

## Source layout

| Source | Responsibility |
| --- | --- |
| [AgentAsToolExample.java](src/main/java/io/github/agentic/ai/examples/AgentAsToolExample.java) | The parent ReAct runtime owns delegation; the provider sees only tool definitions. |
| [ReviewRequest.java](src/main/java/io/github/agentic/ai/examples/ReviewRequest.java) | Structured input exposed in a specialist's generated tool schema. |
| [StructuredDelegation.java](src/main/java/io/github/agentic/ai/examples/StructuredDelegation.java) | Exposes a typed specialist input while keeping parent reasoning out of the child context. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### StructuredDelegation

Exposes a typed specialist input while keeping parent reasoning out of the child context. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/agent-as-tool -Dexample.mainClass=io.github.agentic.ai.examples.StructuredDelegation
```
