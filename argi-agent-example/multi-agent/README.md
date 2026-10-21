# multi-agent

Parallel reviewers feed sequential writing and a bounded revision loop.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/multi-agent
```

## Behavior

Two reviewers run before the writer. The writer makes two bounded revision passes. Inspect `technical_review`, `usability_review`, and `checklist` in the resulting state.

## Source and verification

- [MultiAgent.java](src/main/java/io/github/agentic/ai/examples/MultiAgent.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Parallel reviewers feed sequential writing and a bounded revision loop.
- Custom deterministic merge strategy, bounded parallel reviewers and a condition-based revision loop capped at three iterations.

Limit: Local composition; convergence and review quality depend on model responses. The revision loop has an explicit ceiling.

## Source layout

| Source | Responsibility |
| --- | --- |
| [MultiAgent.java](src/main/java/io/github/agentic/ai/examples/MultiAgent.java) | Independent reviewers feed a writer, followed by a bounded revision loop. |
| [ParallelReview.java](src/main/java/io/github/agentic/ai/examples/ParallelReview.java) | Combines independent reviewers with a concurrency bound and an application merge policy. |
| [ReviewMergeStrategy.java](src/main/java/io/github/agentic/ai/examples/ReviewMergeStrategy.java) | Preserves each review's origin and gives the writer deterministic merge ordering. |
| [RevisionUntilReady.java](src/main/java/io/github/agentic/ai/examples/RevisionUntilReady.java) | Stops on an explicit review marker, with a three-iteration ceiling. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### ParallelReview

Combines independent reviewers with a concurrency bound and an application merge policy. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/multi-agent -Dexample.mainClass=io.github.agentic.ai.examples.ParallelReview
```

### RevisionUntilReady

Stops on an explicit review marker, with a three-iteration ceiling. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/multi-agent -Dexample.mainClass=io.github.agentic.ai.examples.RevisionUntilReady
```
