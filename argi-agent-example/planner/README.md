# planner

Produces validated direct or decomposed plans from an explicit capability catalog.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/planner
```

## Behavior

Default mode is `DECOMPOSE`; `DIRECT` selects a known capability without model planning. Plans are validated against the capability catalog, maximum steps, and maximum dependency depth. Returning a plan does not execute it.

## Source and verification

- [PlannerExample.java](src/main/java/io/github/agentic/ai/examples/PlannerExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Produces validated direct or decomposed plans from an explicit capability catalog.
- Application-owned capability catalog; valid dependency chains; rejection of unregistered targets, missing dependencies, duplicate IDs and cycles.

Limit: Plans are validated data. No trusted executor, scheduling or plan-result persistence is implemented here.

## Source layout

| Source | Responsibility |
| --- | --- |
| [PlanValidationScenarios.java](src/main/java/io/github/agentic/ai/examples/PlanValidationScenarios.java) | Validates plans locally; no capability is executed by validation. |
| [PlannerExample.java](src/main/java/io/github/agentic/ai/examples/PlannerExample.java) | Planning produces data; it never executes the capabilities named by the plan. |
| [PlanningCatalog.java](src/main/java/io/github/agentic/ai/examples/PlanningCatalog.java) | The application declares executable capability names before asking a model to plan. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### PlanValidationScenarios

Validates plans locally; no capability is executed by validation. Runtime: **local**.

```shell
bash tools/run.sh argi-agent-example/planner -Dexample.mainClass=io.github.agentic.ai.examples.PlanValidationScenarios
```
