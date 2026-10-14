# hitl

InterruptableAction requests missing feedback and resumes the same node.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
No API key, container, or external service is required.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-graph-example/hitl
```

## Behavior

The `Approval` action interrupts only while `approved` is missing. Supplying feedback resumes the same thread; false produces `rejected`.

## Source and verification

- [DynamicApproval.java](src/main/java/io/github/agentic/ai/examples/DynamicApproval.java) contains the complete example.
- [DynamicApprovalTest.java](src/test/java/io/github/agentic/ai/examples/DynamicApprovalTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
