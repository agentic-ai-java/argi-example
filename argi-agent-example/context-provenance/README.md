# Context provenance

Tags retrieved documents and child output, constructs two summaries and clamps a forged high-trust claim.

## Requirements

Java 17+, Maven 3.9.1+, and Core revision
`5ec935b384d5379324ee60277ddcd3a6683a861f` built as `2.1.0-RC2-SNAPSHOT`.
No model key, container, database or network service is required.
Follow [source compatibility](../../docs/compatibility.md) to install dependencies.

## Run

From the repository root after dependency bootstrap and reactor installation:

```shell
bash tools/run.sh argi-agent-example/context-provenance
```

## Expected behavior

Application instructions remain trusted; retrieved and child content are untrusted; summaries are derived and retain attribution.

## Verification

```shell
bash tools/mvn.sh -B -pl :example-context-provenance -am verify
```

The tests assert the demonstrated behavior and boundary cases. See
[compatibility.md](../../docs/compatibility.md) for the actual validation record;
commands listed here are instructions, not a claim that execution occurred.

## Limits

Summary text is supplied by the application. This does not run a summarization model, prove prompt-injection immunity or authenticate serialized metadata.

See the [website guide](https://github.com/agentic-ai-java/argi-website/blob/main/docs/frameworks/agent-framework/context-provenance.md).
