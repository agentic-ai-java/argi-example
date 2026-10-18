# parallel-nodes

Fork/join branches, an append reducer, explicit executors, and streamed node output.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/parallel-nodes
```

## Behavior

The stream includes independent stock, credit, and shipping checks followed by `ready=true`. Branch output order is not fixed.

## Source and verification

- [ParallelNodes.java](src/main/java/io/github/agentic/ai/examples/ParallelNodes.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Fork/join branches, an append reducer, explicit executors, and streamed node output.
- Separate stock/credit/shipping checks join before an eligibility branch; test source enumerates all eight input combinations.

Limit: Local business checks and executor ownership; no inventory or payment service is contacted.

## Source layout

| Source | Responsibility |
| --- | --- |
| [OrderChecks.java](src/main/java/io/github/agentic/ai/examples/OrderChecks.java) | Independent local business checks write one result each through an append reducer. |
| [OrderFulfillment.java](src/main/java/io/github/agentic/ai/examples/OrderFulfillment.java) | Joins all checks before choosing whether an order is eligible for fulfillment. |
| [ParallelNodes.java](src/main/java/io/github/agentic/ai/examples/ParallelNodes.java) | Aggregates independent checks using an explicitly owned worker pool. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### OrderFulfillment

Joins all checks before choosing whether an order is eligible for fulfillment. Runtime: **local**.

```shell
bash tools/run.sh argi-graph-example/parallel-nodes -Dexample.mainClass=io.github.agentic.ai.examples.OrderFulfillment
```
