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

- [MultiAgent.java](src/main/java/io/github/agentic/ai/examples/MultiAgent.java) contains the complete example.
- [MultiAgentTest.java](src/test/java/io/github/agentic/ai/examples/MultiAgentTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
