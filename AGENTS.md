# Repository guidance

- Communicate with the user in Chinese. Write Java, comments, configuration descriptions, and documentation in English.
- The sibling `demo` repository is a directory and Maven-layout reference only. Do not read or copy its application code.
- Use `website/docs` as the scenario inventory; resolve outdated package names and APIs against `argi` and `argi-extensions` source.
- Keep every example as a named Maven module with a real entry point, a README, prerequisites, and expected behavior.
- Do not start Docker, OrbStack, databases, registries, or model services to work on this repository.
- Do not execute examples or integration tests unless the user explicitly asks. Static catalog and source checks are safe.
- Never embed credentials. Model and service configuration comes from environment variables.
- Avoid provider SDK objects in checkpoint state; use ARGI messages or ordinary data.
- Use lightweight local observation. Do not add a Prometheus or OpenTelemetry deployment.
- Keep CI enabled. Remote publication and creating a GitHub repository require explicit authorization.
