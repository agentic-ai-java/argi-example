# skills

Loads packaged skills, decorates neutral prompts, and exposes progressive skill disclosure.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Set ARGI_API_KEY and ARGI_MODEL; optionally set ARGI_BASE_URL to an OpenAI-compatible /v1 endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-agent-example/skills
```

## Behavior

The packaged `skills/release-notes/SKILL.md` is discovered and read through the same registry. Classpath extraction uses `target/skills-cache`; the registry is closed after the call.

## Source and verification

- [SkillsExample.java](src/main/java/io/github/agentic/ai/examples/SkillsExample.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Loads packaged skills, decorates neutral prompts, and exposes progressive skill disclosure.

Limit: One packaged skill and progressive disclosure; no untrusted remote skill installation.
