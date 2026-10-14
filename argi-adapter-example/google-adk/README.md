# google-adk

Uses the Google ADK Apigee OpenAI-compatible transport through an ARGI adapter.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-adapter-example/google-adk
```

## Behavior

Uses the Google ADK Apigee OpenAI-compatible transport through an ARGI adapter.

The model is constructed from ARGI_API_KEY, ARGI_MODEL, and ARGI_BASE_URL. Only the selected framework adapter is used. The command logs one model answer. Tests validate configuration and the neutral request contract; they do not certify provider availability.

This is the ADK Apigee OpenAI-compatible path, not native Gemini/Vertex AI authentication. Its capabilities follow the pinned ARGI adapter.

## Source and verification

- [GoogleAdkExample.java](src/main/java/io/github/agentic/ai/examples/GoogleAdkExample.java) contains the complete example.
- [GoogleAdkExampleTest.java](src/test/java/io/github/agentic/ai/examples/GoogleAdkExampleTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
