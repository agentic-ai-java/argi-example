# Tool execution policies

Runs a real AgentToolNode with application-owned trusted identity, opt-in argument validation and explicit business-result mapping.

## Requirements

Java 17+, Maven 3.9.1+, and Core revision
`5ec935b384d5379324ee60277ddcd3a6683a861f` built as `2.1.0-RC2-SNAPSHOT`.
No model key, container, database or network service is required.
Follow [source compatibility](../../docs/compatibility.md) to install dependencies.

## Run

From the repository root after dependency bootstrap and reactor installation:

```shell
bash tools/run.sh argi-agent-example/tool-execution-policies
```

## Expected behavior

Missing identity and revoked permission yield auth-denied with zero callbacks; invalid arguments yield error with zero callbacks; a registered invoice decline yields business-failed; an unregistered decline stays SUCCESS.

## Verification

```shell
bash tools/mvn.sh -B -pl :example-tool-execution-policies -am verify
```

The tests assert the demonstrated behavior and boundary cases. See
[compatibility.md](../../docs/compatibility.md) for the actual validation record;
commands listed here are instructions, not a claim that execution occurred.

## Limits

The ledger is an in-process example. SelectionFailurePolicies deliberately injects selector unavailability; EffectAwareRetries injects an uncertain timeout. No model-quality, real authorization provider or external exactly-once guarantee is demonstrated.

See the [website guide](https://github.com/agentic-ai-java/argi-website/blob/main/docs/frameworks/agent-framework/tool-execution-policies.md).

## Additional local failure drills

```shell
bash tools/run.sh argi-agent-example/tool-execution-policies -Dexample.mainClass=io.github.agentic.ai.examples.EffectAwareRetries
bash tools/run.sh argi-agent-example/tool-execution-policies -Dexample.mainClass=io.github.agentic.ai.examples.SelectionFailurePolicies
```

Read-only retries make two attempts after an injected timeout; unsafe effects stop
at one with `inDoubt=true`. Selector outage keeps all tools under the compatibility
default, keeps only `help` under the mandatory-only policy, or throws before the
downstream invocation under the strict throw policy.

The authorization example uses fixed local identities, not authentication.
Production policies must consult trusted identity and resource permissions on each
attempt. `requireMatchingTenant()` only checks tenant presence at this baseline.
The validation gate emits generic `error` with details; not every ToolOutcome
constant is emitted by every failure path. Inspect `auth-denied` explicitly.
