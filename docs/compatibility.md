# Source compatibility

The examples target the local RC2 API, not the historical snippets verbatim.

| Input | Revision used for API review |
| --- | --- |
| ARGI | `ee72a721cfe6d50184068d053f70784556aee424` |
| ARGI Extensions | `84994ebb71374ebc3dea688ae7f81360934313f9` |
| website | `21975f6313f0ef63542abf110e129bc81a110b64` |
| argi-website (additional planner chapter) | `f8e815190e2dc6afc783fec6a6ec6309c1c000ca` |

Both Maven BOMs are pinned to `2.1.0-RC2-SNAPSHOT`. Since a snapshot coordinate
does not freeze source, CI checks out the revisions above. The local bootstrap
script uses the caller's sibling checkouts and prints their revisions. It does not
switch branches or modify their tracked files. Extensions must be installed with
`-Dargi.version=2.1.0-RC2-SNAPSHOT`; its source parent still defaults to RC1.

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

## Verification boundary

Final authoring review date: **2026-10-14**. After the user's instruction to write
without execution, no examples, test suites, builds, model requests, or service
startup commands were run. Static checks validate catalog completeness, Maven
module wiring, local documentation links, source imports, and resource presence.
Earlier test results cover only an earlier subset and do not certify this expanded
repository. CI is provided for subsequent verification and has not been triggered.

Static review completed for the final tree:

- `python3 tools/check_catalog.py`: 58 example modules and 54 website mappings;
  module entries, backend profile resources, and local documentation links resolve.
- XML parsing: all 63 Maven POMs are well formed.
- `bash -n tools/mvn.sh tools/bootstrap.sh tools/run.sh`: shell syntax passes.
- Ruby Psych parsing: all 22 YAML files are well formed.
- Source review: 90 Java files have balanced lexical delimiters; explicit ARGI imports
  resolve to classes in the current Core, Extensions, or example source trees.
- UTF-8 BOM check passes. These checks do not replace Java compilation or runtime tests.

The dependency bootstrap explicitly disables both formatting and the Maven POM sorter
so invoking it does not reformat the sibling checkouts. Earlier sorter-only edits
created during dependency preparation were reverted; unrelated Core work was preserved.
