# Source compatibility

The examples target the local RC2 API, not the historical snippets verbatim.

| Input | Revision used for API review |
| --- | --- |
| ARGI | `5ec935b384d5379324ee60277ddcd3a6683a861f` |
| ARGI Extensions | `84994ebb71374ebc3dea688ae7f81360934313f9` |
| argi-website baseline | `33d6449f577536645b5ef03e18556d2b1688afa6`, plus the runtime guides in this change |

Both Maven BOMs are pinned to `2.1.0-RC2-SNAPSHOT`. Since a snapshot coordinate
does not freeze source, CI checks out the revisions above. The local bootstrap
script reads the caller's sibling checkouts, prints their revisions, and builds temporary
copies. It does not switch branches or modify their tracked files. Extensions must be installed with
`-Dargi.version=2.1.0-RC2-SNAPSHOT`; its source parent still defaults to RC1.

On 2026-10-18 the Core pin was advanced from `f61a477220b9caec4d63b9469dbab5877eff1df9`
to the current main revision above. It includes run definitions, recovery capability
checks, authorization, business outcomes, selection failure policies, provenance,
batched loops and bounded background tasks. Rebuild local snapshot artifacts before
compiling the new modules. No application data is migrated.

That main revision no longer contains the AgentScope adapter in its reactor or BOM.
The historical example remains under the explicit `legacy-agentscope` Maven profile;
it requires the earlier adapter artifact to be installed separately. Default builds
and bootstrap use the three adapters actually available on current main. This is an
explicit source-baseline migration, not a claim that the legacy adapter was tested
against the new baseline.

## Current update verification

The complete default reactor (63 example modules, excluding historical AgentScope)
and its test sources compile with Java 17 using `bash tools/mvn.sh -B -DskipTests package`.
The catalog check passes with 64 entries, 60 website mappings, 123 main source files
and 84 test files. Runtime execution and coverage are pending explicit execution
authorization. The existing results below remain dated historical evidence and must
not be treated as validation of this update.

The catalog now follows the active `argi-website/docs` inventory, while retaining
existing source-led coverage and historical model/backend validation details.

## Direct replacements from website snippets

- Use `io.github.agentic-ai-java:argi-*` coordinates and
  `io.github.agentic.ai.*` packages.
- Provide an ARGI `ChatClient` with `chatClient(...)`; wrap native clients explicitly.
- Bind tools with `ToolRegistry`. Bridge Spring AI callbacks with
  `SpringAiToolAdapter`; do not pass native callbacks to removed agent builder methods.
- Use `ArgiSkillChatClient` in place of the historical skill advisor.
- Use `argi.*` for Extensions settings. Native Spring AI MCP server settings still
  use `spring.ai.mcp.*`; those are separate configuration owners.
- Resume with the checkpoint cursor returned by `updateState(...)` and
  `withResume()`. Keep the original thread identity.
- RAG graph nodes return Spring AI `Document` values. Convert them to maps at the
  node boundary before the neutral RC2 checkpoint serializer sees them.
- Subscribe to a graph stream once. Subscribing and then calling `blockLast()` on
  the same cold stream starts two executions; the cancellation example avoids this.
- Configure an executor when demonstrating actual parallel branches.
- The observation example uses local Micrometer handlers, without an external
  telemetry deployment.
- Mem0 uses its specialized `SearchRequest.mem0Builder()` and an explicit user
  scope. It is semantic memory, not an ordered chat-message repository.
- The Tair implementation does not support deleting documents through
  `VectorStore.delete`. Its examples use dedicated indexes and retain the sample
  document for inspection.

This is a new example repository: no existing application data is migrated.
When adapting historical snippets, replace the old APIs directly. Keep old
checkpoint backups when upgrading an existing application; the example repository
does not migrate production stores.

## Staged compatibility corrections

`tools/bootstrap.sh` now requires Python 3 and builds temporary copies under
`target/bootstrap`, with automatic cleanup. The version coordinates remain
`2.1.0-RC2-SNAPSHOT`; these are explicitly corrected local artifacts, not a claim
that the unmodified upstream pins already contain the fixes.

1. **Core nested-stream completion:** `tools/build_core.py` adds an empty visible-message
   check to `NodeExecutor`'s terminal-response filter. The pinned runtime can emit a
   completion event with a full origin response and no visible messages. Its parent
   graph previously accessed `streaming.messages().get(0)`, causing an index error.
   `RoutingStreamingTest` reproduced that failure before the correction and passes
   afterward. Routing and embedded support-writer model runs verify the repaired path.
2. **Extensions A2A streaming:** `tools/build_extensions.py` changes the obsolete
   `StreamingOutput.chunk()` call to `StreamingOutput<?>.text()` in the staged copy.
3. **Extensions Sandbox SDK:** the same script declares Sandbox's direct
   `spring-ai-model` dependency, since neutral Core RC2 no longer supplies that SDK
   transitively. The version comes from Extensions' Spring AI BOM.

The scripts reject an unfamiliar streaming implementation instead of silently
applying an uncertain replacement. The original Core and Extensions worktrees are
unchanged. Rebuild through bootstrap on a fresh machine or CI; a previously installed
uncorrected artifact with the same snapshot coordinate is insufficient.

## Verification boundary

Last verification: **2026-10-15**, Java **17.0.19**, Maven **3.9.10**.
Execution was explicitly authorized after the earlier static authoring pass.
The selected Claude Code alias `sonnet` resolved to **`glm-5.3`**. Its gateway
accepted OpenAI-compatible chat requests. Credentials remained in child-process
environments; no key, gateway settings file, or account configuration was committed.
Spring AI request timeouts were set to 120 seconds for the longer model runs.

| Check | Result |
| --- | --- |
| Fixed Core and Extensions dependency builds | Passed with the three staged corrections above |
| Full example reactor, `bash tools/mvn.sh -B -fae install` | Passed, including verification |
| Final `bash tools/mvn.sh -B -fae verify` | Passed; 109 tests, 0 failures, 0 errors, 0 skipped |
| JaCoCo | 1,401 / 1,435 executable lines in 44 measured modules: **97.63%**; every measured module is at least 90% |
| Unmeasured coverage | 15 modules have no tests/execution data; the measured percentage does **not** represent all 59 modules |
| Local entry-point runs | **36 / 36 passed**, including separate persistence prepare/resume processes |
| Model entry-point cases | **34 / 38 passed**; four incomplete gateway-backed cases are listed below |
| Studio HTTP/SSE | Passed: HTTP 200 agent discovery and a real-model SSE answer; application stopped afterward |
| Backend profiles | All 7 memory and 5 vector profiles compiled; backend connectivity was not tested |
| Catalog/import checks | 59 modules, 54 website mappings, 116 main sources, 79 test sources, 31 additional entry points; no wildcard imports |
| Remote CI and vulnerability checks | Not run for these working-tree changes; CI and its coverage gate remain enabled |

Counts are distinct entry-point/argument combinations, not model request counts.
Reruns replace the earlier outcome. A normal process exit alone was insufficient:
empty answers, swallowed model exceptions, pending reapprovals and failed requests
were not counted as successful completed answers. Per-module test, coverage and
runtime outcomes are recorded in [catalog.json](catalog.json).

Successful real-model paths include all four adapters' basic, asynchronous and
streaming requests; strict ticket validation and typed output; ordinary and direct
return tools; actual fail-once tool retry; retrieval evidence; thread isolation and
budgets; long-history summarization; selected and emulated tools; nested delegation;
planner decomposition; bounded revision; routing streams; and support draft review.
The compaction stress-input example retained seven messages, a summary marker and
the first user message. The healthy-primary fallback run does not prove a live
provider failover; injected primary failure is covered by a deterministic test.

### Incomplete real-model cases

| Entry point | Observed outcome |
| --- | --- |
| `ContextCompaction` | Gateway reset/cancelled the connection during the ordinary four-turn conversation; the separate long-transcript `CompactionScenarios` run passed |
| `MultiAgent` | Parallel review/writing run failed with request timeout/connection cancellation; deterministic composition tests pass |
| `ParallelReview` | Gateway reset a parallel review request; deterministic merge/runtime tests pass |
| `AgentApproval EDITED` | Corrected arguments executed `ORDER-43` and returned `Preparing shipment`, but the gateway reset the subsequent model stream before completion; `APPROVED` and `REJECTED` live runs pass, and all three decision paths pass local tests |

The first three were retried without obtaining a complete validated result. These
are unresolved live-provider failures, not successful integration tests. The edited
approval run additionally exposed a possible second approval request; the example
now treats a new interruption as unfinished and never approves it automatically.

### Execution-driven example fixes

- Restored explicit Spring/JUnit/tool annotation imports removed during the earlier
  source-only import cleanup; sorted imports without deleting annotation names.
- Registered the local observation lifecycle listener and added the MongoDB sync
  driver's direct dependency.
- Selected neutral JSON Schema only when the adapter advertises support. The Spring
  AI text fallback includes the schema in the prompt and strictly validates the result;
  it is not provider-enforced structured output.
- Raised adapter scenario output budgets from 160 to 2,048 tokens and reject blank
  or length-truncated final answers.
- Registered the RAG evidence hook at `BEFORE_MODEL`; added runtime request assertions.
- Preserved typed approval feedback instead of overwriting it with `.resume()`,
  passed non-null empty input on resume, and reject a second pending approval.
- Matched the embedded support writer's agent name to its graph node ID.
- Propagated model failures rather than returning successful error-text answers.
- Owned and closed parallel executors; avoided interrupting provider daemon pools
  when a standalone Maven launcher exits.

No Docker/OrbStack, database, Nacos registry, external MCP server, vector backend or
sandbox infrastructure was started. Live A2A/MCP/backend interoperability, performance,
load, stress, capacity, chaos and dependency-CVE checks remain unverified. Local
fixtures and profile compilation cannot establish those properties.

No data migration: these example and launcher changes directly replace the prior
source version. To roll back dependency corrections, restore the preceding bootstrap
implementation and rebuild the original pins; this restores the documented upstream
limitations too. Review and revert only this task's source diff to preserve unrelated
working-tree changes. Temporary worktrees, model-run helpers, logs, backups and
module runtime artifacts are removed after recording the results here.
