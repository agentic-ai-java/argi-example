# agentscope

> Historical compatibility example: Core main at `5ec935b384d5379324ee60277ddcd3a6683a861f`
> no longer contains this adapter or manages it in the BOM. This module is excluded
> from the default reactor. To build it, separately install the adapter from the
> earlier compatible source `f61a477220b9caec4d63b9469dbab5877eff1df9` and select
> `-Plegacy-agentscope`. The current bootstrap does not install that historical
> artifact. No validation of this optional profile is claimed for the new baseline.

Wraps an AgentScope OpenAI-compatible model in the neutral ARGI ChatClient.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-adapter-example/agentscope
```

## Behavior

Wraps an AgentScope OpenAI-compatible model in the neutral ARGI ChatClient.

The model is constructed from ARGI_API_KEY, ARGI_MODEL, and ARGI_BASE_URL. Only the selected framework adapter is used. The command logs one model answer. Tests validate configuration and the neutral request contract; they do not certify provider availability.

## Source and verification

- [AgentScopeExample.java](src/main/java/io/github/agentic/ai/examples/AgentScopeExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Wraps an AgentScope OpenAI-compatible model in the neutral ARGI ChatClient.
- Asynchronous completion, capability-gated streaming, one-event demand, final-response handling, terminal failures and cancellation on timeout.

Limit: Adapter-level capability flags, async completion and demand-controlled streams are demonstrated. Model-specific features and native SDK interoperability have not been exercised.

## Source layout

| Source | Responsibility |
| --- | --- |
| [AgentScopeExample.java](src/main/java/io/github/agentic/ai/examples/AgentScopeExample.java) | Demonstrates native model construction and the neutral ARGI invocation boundary. |
| [AgentScopeScenarios.java](src/main/java/io/github/agentic/ai/examples/AgentScopeScenarios.java) | Exercises neutral async calls and demand-controlled streaming through this adapter. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### AgentScopeScenarios

Exercises neutral async calls and demand-controlled streaming through this adapter. Runtime: **model**.

```shell
bash tools/run.sh argi-adapter-example/agentscope -Dexample.mainClass=io.github.agentic.ai.examples.AgentScopeScenarios
```
