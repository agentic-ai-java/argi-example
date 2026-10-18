# studio-chatbot

Publishes a stateful agent bean through the embedded ARGI Studio.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

The pinned Studio API also requires `ARGI_AGENT_STUDIO_EXECUTION_AUTHTOKEN`.
Set it to a caller-owned value and send the same value in the
`X-Agentic-Studio-Token` header when calling `/list-apps` or `/run_sse`.
The model API key and Studio access token are separate credentials.

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

- [StudioChatbot.java](src/main/java/io/github/agentic/ai/examples/StudioChatbot.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Publishes a stateful agent bean through the embedded ARGI Studio.

Limit: An embedded Studio agent bean; no UI E2E validation, authentication deployment or multi-tenant setup.
