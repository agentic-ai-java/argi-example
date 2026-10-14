# a2a-nacos

Resolves an A2A agent by registered name in Nacos before delegation.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
Nacos A2A discovery and a registered remote agent; set A2A_AGENT_NAME and NACOS_SERVER_ADDR.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/a2a-nacos
```

## Behavior

Discovery resolves A2A_AGENT_NAME through Nacos before constructing the remote agent. Registry writes are disabled for this client example.

## Configuration

- [application.yml](src/main/resources/application.yml): environment variable names and profile settings.

## Source and verification

- [NacosA2aExample.java](src/main/java/io/github/agentic/ai/examples/NacosA2aExample.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
