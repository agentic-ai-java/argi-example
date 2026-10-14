# argi-agent-example

See the [root setup](../README.md) and select one capability below.

- [a2a](a2a/README.md): Discovers a remote A2A agent card and delegates a task over the A2A protocol.
- [agent-as-tool](agent-as-tool/README.md): Registers a specialist as a neutral Tool, letting a coordinator delegate work.
- [agent-hitl](agent-hitl/README.md): Pauses tool calls and accepts approved, rejected, or edited human feedback.
- [agentic-rag](agentic-rag/README.md): Makes retrieval an agent tool and adds evidence through a MessagesModelHook.
- [builtin-tools](builtin-tools/README.md): Registers built-in filesystem, search, and todo tools with neutral bindings.
- [context-compaction](context-compaction/README.md): Uses SummarizationHook to bound model context while retaining recent turns.
- [context-engineering](context-engineering/README.md): Preserves per-thread conversation history and enforces model call budgets.
- [hooks-and-retries](hooks-and-retries/README.md): Combines PII redaction, model/tool budgets, and bounded retries for read-only tools.
- [interceptors](interceptors/README.md): Custom model and tool interceptors measure duration and cache a read-only lookup.
- [multi-agent](multi-agent/README.md): Parallel reviewers feed sequential writing and a bounded revision loop.
- [planner](planner/README.md): Produces validated direct or decomposed plans from an explicit capability catalog.
- [react-tools](react-tools/README.md): Runs a ReAct tool loop against a local order-tracking catalog.
- [routing-streaming](routing-streaming/README.md): Uses LLM routing to choose a specialist and demonstrates neutral streamed messages.
- [skills](skills/README.md): Loads packaged skills, decorates neutral prompts, and exposes progressive skill disclosure.
- [structured-output](structured-output/README.md): Requests a JSON schema and validates the parsed domain record.
- [studio-chatbot](studio-chatbot/README.md): Publishes a stateful agent bean through the embedded ARGI Studio.
- [support-workflow](support-workflow/README.md): Embeds a ReAct writer in a deterministic support workflow with human review.
