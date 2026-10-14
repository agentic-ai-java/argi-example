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

- [PlannerExample.java](src/main/java/io/github/agentic/ai/examples/PlannerExample.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
