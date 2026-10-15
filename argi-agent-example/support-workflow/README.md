# support-workflow

Embeds a ReAct writer in a deterministic support workflow with human review.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/support-workflow
```

## Behavior

Classifies a ticket, embeds a ReAct writer as a graph node, and pauses for review. It returns `ready-to-send` after approval but does not send an email or update an external ticket system.

## Source and verification

- [SupportWorkflow.java](src/main/java/io/github/agentic/ai/examples/SupportWorkflow.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Embeds a ReAct writer in a deterministic support workflow with human review.
- Validated ticket input, draft inspection, separate approved/rejected threads and reviewer metadata persisted before resume.

Limit: Draft and review state only; no email is sent and no external refund is issued.

## Source layout

| Source | Responsibility |
| --- | --- |
| [SupportReview.java](src/main/java/io/github/agentic/ai/examples/SupportReview.java) | Keeps a review decision attached to the saved draft and its checkpoint cursor. |
| [SupportTicket.java](src/main/java/io/github/agentic/ai/examples/SupportTicket.java) | Validated domain input is converted to ordinary checkpoint-compatible data. |
| [SupportWorkflow.java](src/main/java/io/github/agentic/ai/examples/SupportWorkflow.java) | Classifies an issue, drafts a response, then requires review before publication. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### SupportReview

Keeps a review decision attached to the saved draft and its checkpoint cursor. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/support-workflow -Dexample.mainClass=io.github.agentic.ai.examples.SupportReview
```
