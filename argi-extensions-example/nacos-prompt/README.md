# nacos-prompt

Loads and watches a configurable prompt through Nacos and the Extensions prompt factory.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
An existing Nacos Config service; set NACOS_SERVER_ADDR and optional NACOS_USERNAME/NACOS_PASSWORD.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/nacos-prompt
```

## Behavior

Uses data ID `argi.configurable.prompt`, group `DEFAULT_GROUP`, and a JSON array of named templates. The initial CLI call prints the currently resolved prompt and exits; the class also exposes the listener lifecycle for a long-running application.

Publish this JSON through your Nacos administration workflow before running:

```json
[{"name":"support","template":"Explain {question} using a short example."}]
```

A running application should retain its NacosPrompt instance and resolve `render(...)` on each request; caching a ConfigurablePromptTemplate misses subsequent replacement.

## Source and verification

- [NacosPrompt.java](src/main/java/io/github/agentic/ai/examples/NacosPrompt.java) contains the complete example.
- [NacosPromptTest.java](src/test/java/io/github/agentic/ai/examples/NacosPromptTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
