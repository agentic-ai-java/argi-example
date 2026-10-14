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

- [ReactTools.java](src/main/java/io/github/agentic/ai/examples/ReactTools.java) contains the complete example.
- [ReactToolsTest.java](src/test/java/io/github/agentic/ai/examples/ReactToolsTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
