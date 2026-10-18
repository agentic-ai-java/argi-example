# ARGI Examples

Examples for the ARGI core runtime and ARGI Extensions, organized as a Maven reactor:
**root project → capability group → independent example module**.
Only the sibling `demo` repository's directory and Maven aggregation layout was used.
The implementations come from ARGI/Extensions API contracts and the scenarios in
`argi-website/docs`; no demo application source was reused.

The repository contains **64 example modules** (63 in the default reactor and one
historical AgentScope module behind `legacy-agentscope`), with **12 additional backend
profile configurations** across the memory and vector modules. All nine website Graph
scenario pages have dedicated examples. [Website coverage](docs/website-coverage.md)
maps chapters to relevant modules; a chapter mapping is not proof that every feature
in that chapter is implemented. [Behavioral coverage](docs/feature-coverage.md) records
the demonstrated paths, additional entry points, and limits of every module.
[catalog.json](docs/catalog.json) is the machine-readable source and scenario index.

Expanded scenarios separate application data, repositories, tools, state inspection,
and workflow orchestration where they have distinct responsibilities. Small introductory
examples remain small. Both production and test Java sources use explicit imports;
the static catalog check rejects ordinary and static wildcard imports.

## Requirements and setup

- JDK 17 or newer; Maven 3.9.1 or newer.
- ARGI and Extensions `2.1.0-RC2-SNAPSHOT` built from compatible source revisions.
- Python 3 for staged dependency builds and the static catalog check.
- Model-backed examples need `ARGI_API_KEY` and `ARGI_MODEL`; `ARGI_BASE_URL` defaults
  to `https://api.openai.com/v1`. The Spring AI factory accepts
  `ARGI_TIMEOUT_SECONDS` (default `60`); this validation used `120` for longer model turns.
- External integration examples need caller-managed services. Docker is required
  only by the optional Docker execution example or the chosen sandbox infrastructure.

Source checkouts are expected next to this repository:

```text
open_source/
├── argi/
├── argi-extensions/
├── argi-example/
├── argi-website/     # documentation input, not a build dependency
└── demo/             # structural reference, not a build dependency
```

Build dependencies from the revisions in [compatibility.md](docs/compatibility.md),
then install and run the selected module:

```shell
# Inspect source/document wiring; this does not execute Java or contact services.
python3 tools/check_catalog.py

# Install matching source dependencies without starting services or running tests.
bash tools/bootstrap.sh

# Install the example reactor; required by modules that reuse another example's factory.
bash tools/mvn.sh -B install

# Run a model-free example explicitly.
bash tools/run.sh argi-graph-example/hello-graph
```

`tools/mvn.sh` uses the installed mise-managed Java 17.0.19 and Maven 3.9.10 when
mise is available; otherwise it uses your `mvn`. It does not install SDKs. Set
`ARGI_CORE_DIR` and `ARGI_EXTENSIONS_DIR` if the source checkouts live elsewhere.
The source revision and RC2 migration notes are in [compatibility.md](docs/compatibility.md).
The `recoverable-runs` module requires the pinned Core run/task-contract commit;
an older RC2 snapshot with the same Maven version does not contain those classes.

The root imports **both** `argi-bom` and `argi-extensions-bom`, with the Core BOM
first. The bootstrap command overrides Extensions' RC1 default with the matching
RC2 core version. It builds temporary source copies with three compatibility
corrections documented in [compatibility.md](docs/compatibility.md), then removes
those copies. Sibling tracked source files are never modified. All examples use `io.github.agentic.ai.*` and neutral model/tool
contracts at the ARGI runtime boundary.

## Example catalog

Runtime labels: **local** needs no model or service; **model** uses real provider
credentials; **service** needs the external service described in its README and may
also need a model. Tests use local fixtures where provided; sample main methods use
real APIs rather than mock responses.


### argi-adapter-example

| Example | Runtime | Demonstrates |
| --- | --- | --- |
| [agentscope](argi-adapter-example/agentscope/README.md) | model | Wraps an AgentScope OpenAI-compatible model in the neutral ARGI ChatClient. |
| [google-adk](argi-adapter-example/google-adk/README.md) | model | Uses the Google ADK Apigee OpenAI-compatible transport through an ARGI adapter. |
| [langchain4j](argi-adapter-example/langchain4j/README.md) | model | Wraps LangChain4j chat and streaming models in the neutral ARGI ChatClient. |
| [spring-ai](argi-adapter-example/spring-ai/README.md) | model | Wraps a Spring AI OpenAI-compatible model in the neutral ARGI ChatClient. |


### argi-agent-example

| Example | Runtime | Demonstrates |
| --- | --- | --- |
| [a2a](argi-agent-example/a2a/README.md) | service | Discovers a remote A2A agent card and delegates a task over the A2A protocol. |
| [agent-as-tool](argi-agent-example/agent-as-tool/README.md) | model | Registers a specialist as a neutral Tool, letting a coordinator delegate work. |
| [agent-hitl](argi-agent-example/agent-hitl/README.md) | model | Pauses tool calls and accepts approved, rejected, or edited human feedback. |
| [agentic-rag](argi-agent-example/agentic-rag/README.md) | model | Makes retrieval an agent tool and adds evidence through a MessagesModelHook. |
| [builtin-tools](argi-agent-example/builtin-tools/README.md) | model | Registers built-in filesystem, search, and todo tools with neutral bindings. |
| [context-compaction](argi-agent-example/context-compaction/README.md) | model | Uses SummarizationHook to bound model context while retaining recent turns. |
| [context-engineering](argi-agent-example/context-engineering/README.md) | model | Preserves per-thread conversation history and enforces model call budgets. |
| [hooks-and-retries](argi-agent-example/hooks-and-retries/README.md) | model | Combines PII redaction, model/tool budgets, and bounded retries for read-only tools. |
| [interceptors](argi-agent-example/interceptors/README.md) | model | Custom model and tool interceptors measure duration and cache a read-only lookup. |
| [multi-agent](argi-agent-example/multi-agent/README.md) | model | Parallel reviewers feed sequential writing and a bounded revision loop. |
| [planner](argi-agent-example/planner/README.md) | model | Produces validated direct or decomposed plans from an explicit capability catalog. |
| [react-tools](argi-agent-example/react-tools/README.md) | model | Runs a ReAct tool loop against a local order-tracking catalog. |
| [routing-streaming](argi-agent-example/routing-streaming/README.md) | model | Uses LLM routing to choose a specialist and demonstrates neutral streamed messages. |
| [skills](argi-agent-example/skills/README.md) | model | Loads packaged skills, decorates neutral prompts, and exposes progressive skill disclosure. |
| [structured-output](argi-agent-example/structured-output/README.md) | model | Requests a JSON schema and validates the parsed domain record. |
| [studio-chatbot](argi-agent-example/studio-chatbot/README.md) | model | Publishes a stateful agent bean through the embedded ARGI Studio. |
| [support-workflow](argi-agent-example/support-workflow/README.md) | model | Embeds a ReAct writer in a deterministic support workflow with human review. |


### argi-extensions-example

| Example | Runtime | Demonstrates |
| --- | --- | --- |
| [a2a-nacos](argi-extensions-example/a2a-nacos/README.md) | service | Resolves an A2A agent by registered name in Nacos before delegation. |
| [docker-execution](argi-extensions-example/docker-execution/README.md) | service | Executes a fixed Python calculation through the Extensions Docker code executor. |
| [document-parsing](argi-extensions-example/document-parsing/README.md) | local | Parses UTF-8 text and JSON records while preserving source metadata. |
| [http-node](argi-extensions-example/http-node/README.md) | service | Retrieves an HTTP order resource as a graph step with explicit timeout and network policy. |
| [jdbc-chat-memory](argi-extensions-example/jdbc-chat-memory/README.md) | local | Persists a bounded Spring AI conversation with the Extensions H2 repository. |
| [jdbc-checkpoint](argi-extensions-example/jdbc-checkpoint/README.md) | local | Restores a pending approval through an H2 JDBC checkpoint saver and a new graph. |
| [mcp-client](argi-extensions-example/mcp-client/README.md) | service | Initializes a Streamable HTTP MCP client and enumerates all tool pages. |
| [mcp-discovery](argi-extensions-example/mcp-discovery/README.md) | service | Discovers registered MCP tools through Nacos and adapts callbacks for ReAct. |
| [mcp-gateway](argi-extensions-example/mcp-gateway/README.md) | service | Aggregates Nacos MCP services into independently routed SSE and Streamable endpoints. |
| [mcp-registry](argi-extensions-example/mcp-registry/README.md) | service | Publishes a read-only MCP tool and registers the server in Nacos. |
| [mcp-router](argi-extensions-example/mcp-router/README.md) | service | Exposes semantic MCP routing tools to a neutral ARGI agent. |
| [mem0-memory](argi-extensions-example/mem0-memory/README.md) | service | Writes semantic memories to Mem0 and retrieves them with an explicit user scope. |
| [memory-backends](argi-extensions-example/memory-backends/README.md) | service | Profile-selected Redis, MongoDB, Elasticsearch, TableStore, Memcached, and JDBC chat repositories. |
| [mongodb-checkpoint](argi-extensions-example/mongodb-checkpoint/README.md) | service | Persists neutral graph state in the Extensions MongoDB checkpoint saver. |
| [nacos-prompt](argi-extensions-example/nacos-prompt/README.md) | service | Loads and watches a configurable prompt through Nacos and the Extensions prompt factory. |
| [rag-retrieval](argi-extensions-example/rag-retrieval/README.md) | service | Expands a query with HyDE and retrieves evidence through the RAG graph node. |
| [redis-checkpoint](argi-extensions-example/redis-checkpoint/README.md) | service | Stores and reloads neutral graph checkpoints with the Extensions Redis saver. |
| [sandbox-tools](argi-extensions-example/sandbox-tools/README.md) | service | Adapts an Extensions sandbox Python tool into a neutral ReAct agent. |
| [tair-vector-store](argi-extensions-example/tair-vector-store/README.md) | service | Writes real embeddings to a Tair Vector index and searches for relevant documents. |
| [vector-backends](argi-extensions-example/vector-backends/README.md) | service | Profile-selected AnalyticDB, OceanBase, OpenSearch, TableStore, and Tair indexing and search. |


### argi-graph-example

| Example | Runtime | Demonstrates |
| --- | --- | --- |
| [cancellation](argi-graph-example/cancellation/README.md) | local | Cancels exactly one graph subscription and releases its asynchronous worker. |
| [conditional-routing](argi-graph-example/conditional-routing/README.md) | local | Routes support tickets into urgent and normal queues using conditional edges. |
| [hello-graph](argi-graph-example/hello-graph/README.md) | local | A two-step model-free workflow that normalizes a name and creates a greeting. |
| [hitl](argi-graph-example/hitl/README.md) | local | InterruptableAction requests missing feedback and resumes the same node. |
| [human-in-the-loop](argi-graph-example/human-in-the-loop/README.md) | local | Pauses before a refund decision and resumes from the same checkpoint. |
| [observation](argi-graph-example/observation/README.md) | local | Records graph observations with a local Micrometer handler and no telemetry backend. |
| [parallel-nodes](argi-graph-example/parallel-nodes/README.md) | local | Fork/join branches, an append reducer, explicit executors, and streamed node output. |
| [parallel-subgraph](argi-graph-example/parallel-subgraph/README.md) | local | Joins order checks and creates a receipt in a nested graph. |
| [persistence](argi-graph-example/persistence/README.md) | local | File checkpoints, thread identity, history inspection, and recovery with a fresh graph. |
| [plantuml](argi-graph-example/plantuml/README.md) | local | Exports PlantUML and Mermaid representations without invoking the graph. |
| [runtime-store](argi-graph-example/runtime-store/README.md) | local | Separates transient invocation metadata, thread state, and long-lived namespaced memory. |
| [recoverable-runs](argi-graph-example/recoverable-runs/README.md) | local | Run/task admission, deduplication, leases, revision conflicts and atomic checkpoint/journal/outbox writes. |
| [scheduling](argi-graph-example/scheduling/README.md) | local | Schedules a graph at a fixed delay and shuts down the task manager after completion. |
| [spring-ai-integration](argi-graph-example/spring-ai-integration/README.md) | model | Adapts a Spring AI model into a neutral streaming LlmNode and consumes graph events. |
| [state-serialization](argi-graph-example/state-serialization/README.md) | local | Append and replace reducers plus neutral message serialization round trips. |
| [subgraph-as-compiled-graph](argi-graph-example/subgraph-as-compiled-graph/README.md) | local | Embeds a compiled child directly and demonstrates reusable state transformation. |
| [subgraph-as-node](argi-graph-example/subgraph-as-node/README.md) | local | Explicitly maps parent input/output to a child graph with private state. |
| [subgraphs](argi-graph-example/subgraphs/README.md) | local | A StateGraph subgraph shares parent state and produces a normalized invoice. |

## Running model and service examples

Inject credentials through your shell or credential manager. Do not put them in
source files, Maven command arguments, or committed `.env` files. For example,
after configuring your environment:

```shell
bash tools/run.sh argi-agent-example/react-tools
bash tools/run.sh argi-agent-example/studio-chatbot
```

Studio serves `/chatui/index.html` on port 8080 and discovers the `studio_support`
agent bean. `SERVER_PORT` overrides the port. Studio uses the upstream bundled UI;
this repository does not copy a demo frontend.

The memory/vector backend modules select one starter at a time. Match the Maven
profile (dependency selection) and Spring profile (configuration selection):

```shell
bash tools/run.sh argi-extensions-example/memory-backends \
  -Pmemory-redis -Dexec.args=--spring.profiles.active=redis

bash tools/run.sh argi-extensions-example/vector-backends \
  -Pvector-oceanbase -Dexec.args=--spring.profiles.active=oceanbase
```

Their READMEs list every backend and required environment variable configuration.
Provision external services separately. No repository command starts OrbStack,
Docker, Nacos, Redis, MongoDB, Elasticsearch, Tair, or a model server.

The MCP modules demonstrate this composition:

1. `mcp-registry` publishes the read-only `lookup` tool and registers `argi-catalog` in Nacos.
2. `mcp-client` connects directly; `mcp-discovery` resolves it through Nacos.
3. `mcp-router` exposes semantic service-routing tools to a ReAct agent.
4. `mcp-gateway` exposes independently routed Streamable and SSE endpoints.

Each component has its own port/configuration and is started explicitly by its caller.

## Checks and CI

```shell
# Static inventory and document links only.
make catalog

# Optional compilation without tests or sample execution.
make compile

# Optional tests and JaCoCo verification; never starts external infrastructure.
make verify
```

Local execution was authorized and performed on **2026-10-15**, using the selected
Claude Code model (`sonnet` resolved to `glm-5.3`) through its OpenAI-compatible gateway.
Credentials were passed only through child-process environments; neither the settings
file nor credentials were copied into this repository. The normal examples continue
to use `ARGI_API_KEY`, `ARGI_MODEL` and `ARGI_BASE_URL`.

The full reactor and all 12 backend profiles build successfully. All 109 local tests
and all 36 local entry-point runs pass. Of 38 real-model cases, 34 pass and four remain
incomplete after gateway resets/timeouts. Studio HTTP/SSE also passed.
The 90% JaCoCo line threshold remains enabled for bundles with execution data;
modules without tests are explicitly outside the measured percentage.
[Compatibility and validation results](docs/compatibility.md#verification-boundary)
record exact counts, failed gateway requests and unexecuted external-service scenarios.
No database, Nacos, MCP server, Docker daemon or sandbox infrastructure was started.
Performance, load, capacity, chaos, dependency-vulnerability and remote-CI checks
are not claimed by this local execution.

[GitHub Actions](.github/workflows/verify.yml) keeps CI enabled for pushes and pull
requests. It installs pinned upstream source revisions, verifies the examples, and
compiles every backend profile. CI is enabled; the current working-tree changes have not
been validated by a remote run. No CI result is inferred from source inspection.

## Adding an example

Add a capability-focused module, a Java main entry point, a README with prerequisites
and observable results, and tests for nontrivial local behavior. Register the module
in its group POM and update `docs/catalog.json`, `docs/website-coverage.md`, and
`docs/feature-coverage.md`. Register all supporting sources, tests, and additional
main methods in the catalog, with their demonstrated behavior and limitations.
Use a real integration in integration scenarios. Label local corpora, fault injection,
and emulation explicitly; keep mock providers in `src/test`. Do not use wildcard imports.

## Cleanup and rollback

Generated diagrams, checkpoints, workspaces, and reports live under each module's
`target/`. They can be removed after inspection. External-service examples document
which sample records they retain; never clear an entire production store to clean
up an example. This is a new repository, with no migration of existing application
data. Remove or revert these new files to roll back the authoring changes. Sibling
repository source files are not part of this change.

## License

[Apache License 2.0](LICENSE).

## Current main runtime examples

| Module | Demonstrated behavior |
| --- | --- |
| [tool-execution-policies](argi-agent-example/tool-execution-policies/README.md) | Runs a real AgentToolNode with application-owned trusted identity, opt-in argument validation and explicit business-result mapping. |
| [context-provenance](argi-agent-example/context-provenance/README.md) | Tags retrieved documents and child output, constructs two summaries and clamps a forged high-trust claim. |
| [background-tasks](argi-agent-example/background-tasks/README.md) | Uses one execution slot and one waiting slot to demonstrate capacity and duplicate-ID rejection, completion and explicit result removal. |
| [batched-array-loop](argi-agent-example/batched-array-loop/README.md) | Drives the actual LoopMode.array initialization and dispatch strategy with ordered JSON input. |
| [versioned-recovery](argi-graph-example/versioned-recovery/README.md) | Serializes exact graph, tool catalog, codec and application references and resolves them against an upgraded registry. |
