# human-in-the-loop

Pauses before a refund decision and resumes from the same checkpoint.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/human-in-the-loop
```

## Behavior

The saved state first contains `awaiting-approval`. Explicit feedback changes the final result to `refunded` or `rejected`.

## Source and verification

- [HumanInTheLoop.java](src/main/java/io/github/agentic/ai/examples/HumanInTheLoop.java) contains the complete example.
- [HumanInTheLoopTest.java](src/test/java/io/github/agentic/ai/examples/HumanInTheLoopTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
