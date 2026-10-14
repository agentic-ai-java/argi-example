# argi-extensions-example

See the [root setup](../README.md) and select one capability below.

- [a2a-nacos](a2a-nacos/README.md): Resolves an A2A agent by registered name in Nacos before delegation.
- [docker-execution](docker-execution/README.md): Executes a fixed Python calculation through the Extensions Docker code executor.
- [document-parsing](document-parsing/README.md): Parses UTF-8 text and JSON records while preserving source metadata.
- [http-node](http-node/README.md): Retrieves an HTTP order resource as a graph step with explicit timeout and network policy.
- [jdbc-chat-memory](jdbc-chat-memory/README.md): Persists a bounded Spring AI conversation with the Extensions H2 repository.
- [jdbc-checkpoint](jdbc-checkpoint/README.md): Restores a pending approval through an H2 JDBC checkpoint saver and a new graph.
- [mcp-client](mcp-client/README.md): Initializes a Streamable HTTP MCP client and enumerates all tool pages.
- [mcp-discovery](mcp-discovery/README.md): Discovers registered MCP tools through Nacos and adapts callbacks for ReAct.
- [mcp-gateway](mcp-gateway/README.md): Aggregates Nacos MCP services into independently routed SSE and Streamable endpoints.
- [mcp-registry](mcp-registry/README.md): Publishes a read-only MCP tool and registers the server in Nacos.
- [mcp-router](mcp-router/README.md): Exposes semantic MCP routing tools to a neutral ARGI agent.
- [mem0-memory](mem0-memory/README.md): Writes semantic memories to Mem0 and retrieves them with an explicit user scope.
- [memory-backends](memory-backends/README.md): Profile-selected Redis, MongoDB, Elasticsearch, TableStore, Memcached, and JDBC chat repositories.
- [mongodb-checkpoint](mongodb-checkpoint/README.md): Persists neutral graph state in the Extensions MongoDB checkpoint saver.
- [nacos-prompt](nacos-prompt/README.md): Loads and watches a configurable prompt through Nacos and the Extensions prompt factory.
- [rag-retrieval](rag-retrieval/README.md): Expands a query with HyDE and retrieves evidence through the RAG graph node.
- [redis-checkpoint](redis-checkpoint/README.md): Stores and reloads neutral graph checkpoints with the Extensions Redis saver.
- [sandbox-tools](sandbox-tools/README.md): Adapts an Extensions sandbox Python tool into a neutral ReAct agent.
- [tair-vector-store](tair-vector-store/README.md): Writes real embeddings to a Tair Vector index and searches for relevant documents.
- [vector-backends](vector-backends/README.md): Profile-selected AnalyticDB, OceanBase, OpenSearch, TableStore, and Tair indexing and search.
