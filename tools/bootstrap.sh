#!/usr/bin/env bash
set -euo pipefail
# Install source dependencies only. No services, containers, examples, or tests are started.
repo_root="$(cd -- "$(dirname -- "$0")/.." && pwd)"
core_dir="${ARGI_CORE_DIR:-$repo_root/../argi}"
extensions_dir="${ARGI_EXTENSIONS_DIR:-$repo_root/../argi-extensions}"
version="2.1.0-RC2-SNAPSHOT"
for source_dir in "$core_dir" "$extensions_dir"; do
  test -f "$source_dir/pom.xml" || { printf 'Missing source checkout: %s\n' "$source_dir" >&2; exit 1; }
  git -C "$source_dir" rev-parse HEAD
done
core_modules=":argi-bom,:argi-agent-framework,:argi-studio,:argi-starter-builtin-nodes,:argi-adapter-spring-ai,:argi-adapter-langchain4j,:argi-adapter-google-adk,:argi-adapter-agentscope"
bash "$repo_root/tools/mvn.sh" -B -f "$core_dir/pom.xml" -pl "$core_modules" -am \
  -Drevision="$version" -Dmaven.test.skip=true -Dspotless.skip=true -Dspring-javaformat.skip=true -Dmaven.sorter.skip=true install
# All optional integrations used in the catalog, including profile-selected backends.
extension_modules=":argi-extensions-bom,:argi-graph-persistence-jdbc,:argi-graph-persistence-redis,:argi-graph-persistence-mongodb,:argi-code-executor-docker,:argi-sandbox,:argi-extensions-model,:argi-rag,:argi-graph-node-network,:argi-graph-node-rag,:argi-prompt-nacos,:argi-mcp-common,:argi-tair-store,:argi-starter-a2a-nacos,:argi-starter-mcp-registry,:argi-starter-mcp-distributed,:argi-starter-mcp-router,:argi-starter-mcp-gateway,:argi-starter-model-chat-memory-mem0,:argi-starter-model-chat-memory-repository-redis,:argi-starter-model-chat-memory-repository-mongodb,:argi-starter-model-chat-memory-repository-elasticsearch,:argi-starter-model-chat-memory-repository-tablestore,:argi-starter-model-chat-memory-repository-memcached,:argi-starter-model-chat-memory-repository-jdbc,:argi-starter-vector-store-analyticdb,:argi-starter-vector-store-oceanbase,:argi-starter-vector-store-opensearch,:argi-starter-vector-store-tablestore,:argi-starter-vector-store-tair"
bash "$repo_root/tools/mvn.sh" -B -f "$extensions_dir/pom.xml" -pl "$extension_modules" -am \
  -Drevision="$version" -Dargi.version="$version" -Dmaven.test.skip=true \
  -Dspotless.skip=true -Dspring-javaformat.skip=true -Dmaven.sorter.skip=true install
