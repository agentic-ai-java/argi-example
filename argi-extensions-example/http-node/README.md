# http-node

Retrieves an HTTP order resource as a graph step with explicit timeout and network policy.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
An existing tracking HTTP endpoint; set TRACKING_BASE_URL and ALLOW_PRIVATE_NETWORK=true only for a trusted local endpoint.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/http-node
```

## Behavior

Requests `/orders/ORDER-42` from TRACKING_BASE_URL. The response is stored as `tracking`. Local/private network access is disabled unless ALLOW_PRIVATE_NETWORK=true. The test supplies its own local HTTP fixture.

## Source and verification

- [HttpNodeExample.java](src/main/java/io/github/agentic/ai/examples/HttpNodeExample.java) contains the complete example.
- [HttpNodeExampleTest.java](src/test/java/io/github/agentic/ai/examples/HttpNodeExampleTest.java) covers the local contract.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
