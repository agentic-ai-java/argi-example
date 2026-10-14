# docker-execution

Executes a fixed Python calculation through the Extensions Docker code executor.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
A running Docker daemon and a pre-pulled Python image; set DOCKER_HOST for the daemon socket and optionally ARGI_DOCKER_IMAGE.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/docker-execution
```

## Behavior

The fixed Python expression is sum([19, 23]), so successful output contains 42. Execution is bounded to 15 seconds and 4096 output bytes. The existing daemon and image are caller-managed; no script starts Docker or pulls images.

## Source and verification

- [DockerExecution.java](src/main/java/io/github/agentic/ai/examples/DockerExecution.java) contains the complete example.
- [DockerExecutionTest.java](src/test/java/io/github/agentic/ai/examples/DockerExecutionTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
