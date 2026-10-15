# argi-graph-example

See the [root setup](../README.md) and select one capability below.

- [cancellation](cancellation/README.md): Cancels exactly one graph subscription and releases its asynchronous worker.
- [conditional-routing](conditional-routing/README.md): Routes support tickets into urgent and normal queues using conditional edges.
- [hello-graph](hello-graph/README.md): A two-step model-free workflow that normalizes a name and creates a greeting.
- [hitl](hitl/README.md): InterruptableAction requests missing feedback and resumes the same node.
- [human-in-the-loop](human-in-the-loop/README.md): Pauses before a refund decision and resumes from the same checkpoint.
- [observation](observation/README.md): Records graph observations with a local Micrometer handler and no telemetry backend.
- [parallel-nodes](parallel-nodes/README.md): Fork/join branches, an append reducer, explicit executors, and streamed node output.
- [parallel-subgraph](parallel-subgraph/README.md): Joins order checks and creates a receipt in a nested graph.
- [persistence](persistence/README.md): File checkpoints, thread identity, history inspection, and recovery with a fresh graph.
- [plantuml](plantuml/README.md): Exports PlantUML and Mermaid representations without invoking the graph.
- [recoverable-runs](recoverable-runs/README.md): Run/task storage, submission deduplication, leases, revision conflicts and atomic commits.
- [runtime-store](runtime-store/README.md): Separates transient invocation metadata, thread state, and long-lived namespaced memory.
- [scheduling](scheduling/README.md): Schedules a graph at a fixed delay and shuts down the task manager after completion.
- [spring-ai-integration](spring-ai-integration/README.md): Adapts a Spring AI model into a neutral streaming LlmNode and consumes graph events.
- [state-serialization](state-serialization/README.md): Append and replace reducers plus neutral message serialization round trips.
- [subgraph-as-compiled-graph](subgraph-as-compiled-graph/README.md): Embeds a compiled child directly and demonstrates reusable state transformation.
- [subgraph-as-node](subgraph-as-node/README.md): Explicitly maps parent input/output to a child graph with private state.
- [subgraphs](subgraphs/README.md): A StateGraph subgraph shares parent state and produces a normalized invoice.
