# react-tools

Runs a ReAct tool loop against a local order-tracking catalog.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/react-tools
```

## Behavior

The application owns a small order catalog. The real model requests `track_order`, ARGI executes it, and the model consumes the paired result. Unknown IDs return a tool error; they do not invent a shipment.

## Source and verification

- [ReactTools.java](src/main/java/io/github/agentic/ai/examples/ReactTools.java) is the primary entry point.
- [ReactToolsTest.java](src/test/java/io/github/agentic/ai/examples/ReactToolsTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Runs a ReAct tool loop against a local order-tracking catalog.
- Tool schema and argument validation; malformed JSON and missing records; unknown registry bindings; result/call identity; direct tool return.

Limit: A local order repository; no commerce API or irreversible order mutation.

## Source layout

| Source | Responsibility |
| --- | --- |
| [DirectTrackingTool.java](src/main/java/io/github/agentic/ai/examples/DirectTrackingTool.java) | Returns the application result directly instead of asking the model to rewrite it. |
| [OrderRecord.java](src/main/java/io/github/agentic/ai/examples/OrderRecord.java) | Immutable application data returned by the tracking repository. |
| [OrderRepository.java](src/main/java/io/github/agentic/ai/examples/OrderRepository.java) | A local fixture repository; replacing it does not change the tool schema. |
| [ReactTools.java](src/main/java/io/github/agentic/ai/examples/ReactTools.java) | A delivery assistant whose tracking results come from an application tool. |
| [ToolScenarios.java](src/main/java/io/github/agentic/ai/examples/ToolScenarios.java) | Exercises successful calls, invalid arguments, missing data and unknown tool names. |
| [TrackingTool.java](src/main/java/io/github/agentic/ai/examples/TrackingTool.java) | Validates model-supplied arguments before looking up an application record. |

## Additional entry points

Run from the repository root after installing the reactor dependencies. Select an entry point explicitly. The validation record distinguishes completed runs from unexecuted or failed scenarios.

### DirectTrackingTool

Returns the application result directly instead of asking the model to rewrite it. Runtime: **model**.

```shell
bash tools/run.sh argi-agent-example/react-tools -Dexample.mainClass=io.github.agentic.ai.examples.DirectTrackingTool
```

### ToolScenarios

Exercises successful calls, invalid arguments, missing data and unknown tool names. Runtime: **local**.

```shell
bash tools/run.sh argi-agent-example/react-tools -Dexample.mainClass=io.github.agentic.ai.examples.ToolScenarios
```
