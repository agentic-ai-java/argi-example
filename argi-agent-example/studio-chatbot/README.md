# studio-chatbot

Publishes a stateful agent bean through the embedded ARGI Studio.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/studio-chatbot
```

## Behavior

Serves Studio on port 8080 (override SERVER_PORT). The `studio_support` agent is discovered from the application context. The UI path is `/chatui/index.html`; `/list-apps` lists the registered agent. Memory is process-local.

## Configuration

- [application.yml](src/main/resources/application.yml): environment variable names and profile settings.

## Source and verification

- [StudioChatbot.java](src/main/java/io/github/agentic/ai/examples/StudioChatbot.java) contains the complete example.
- [StudioChatbotTest.java](src/test/java/io/github/agentic/ai/examples/StudioChatbotTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
