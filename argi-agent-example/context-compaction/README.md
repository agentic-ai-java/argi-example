# context-compaction

Uses SummarizationHook to bound model context while retaining recent turns.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/context-compaction
```

## Behavior

The summary threshold is 1500 tokens, with four recent messages and the first user message retained. Short sample inputs might not trigger summarization; send a longer conversation to observe it.

## Source and verification

- [ContextCompaction.java](src/main/java/io/github/agentic/ai/examples/ContextCompaction.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Uses SummarizationHook to bound model context while retaining recent turns.
- Long synthetic project history crosses the summary threshold; the scenario checks the summary marker and preservation of the first user message.

Limit: Summary content depends on the real model; input size is estimated and no token-cost saving is claimed.

## Source layout

| Source | Responsibility |
| --- | --- |
| [CompactionScenarios.java](src/main/java/io/github/agentic/ai/examples/CompactionScenarios.java) | Supplies a long transcript and checks the actual summary marker in saved state. |
| [ContextCompaction.java](src/main/java/io/github/agentic/ai/examples/ContextCompaction.java) | Summaries and recent messages remain in the same saved conversation thread. |
| [ProjectTranscript.java](src/main/java/io/github/agentic/ai/examples/ProjectTranscript.java) | Synthetic project history large enough to cross the configured summary threshold. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### CompactionScenarios

Supplies a long transcript and checks the actual summary marker in saved state. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/context-compaction -Dexample.mainClass=io.github.agentic.ai.examples.CompactionScenarios
```
