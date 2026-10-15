# memory-backends

Profile-selected Redis, MongoDB, Elasticsearch, TableStore, Memcached, and JDBC chat repositories.

## Requirements

Java 17+, Maven 3.9.1+, and the matching local ARGI/Extensions RC2 artifacts.
The selected repository service and one Maven/Spring profile pair. See the backend configuration table below.

See the [repository setup](../../README.md) before running a module.

## Run

From the repository root, after installing the example reactor:

```shell
bash tools/run.sh argi-extensions-example/memory-backends -Pmemory-redis -Dexec.args=--spring.profiles.active=redis
```

## Behavior

Profile-selected Redis, MongoDB, Elasticsearch, TableStore, Memcached, and JDBC chat repositories.

## Backend profiles

Select exactly one Maven profile and the matching Spring profile:

| Backend | Maven profile | Spring profile | Configuration |
| --- | --- | --- | --- |
| redis | `memory-redis` | `redis` | [application-redis.yml](src/main/resources/application-redis.yml) |
| mongodb | `memory-mongodb` | `mongodb` | [application-mongodb.yml](src/main/resources/application-mongodb.yml) |
| elasticsearch | `memory-elasticsearch` | `elasticsearch` | [application-elasticsearch.yml](src/main/resources/application-elasticsearch.yml) |
| tablestore | `memory-tablestore` | `tablestore` | [application-tablestore.yml](src/main/resources/application-tablestore.yml) |
| memcached | `memory-memcached` | `memcached` | [application-memcached.yml](src/main/resources/application-memcached.yml) |
| mysql | `memory-mysql` | `mysql` | [application-mysql.yml](src/main/resources/application-mysql.yml) |
| postgresql | `memory-postgresql` | `postgresql` | [application-postgresql.yml](src/main/resources/application-postgresql.yml) |

The runner writes three question/answer pairs with a four-message window, reads them through a recreated facade, checks a second conversation, and clears only its two unique conversation IDs. No in-memory fallback is provided. Omitting the backend profile fails to supply the repository bean. The Redis cluster/TLS variant is in [redis-cluster.yml](config/redis-cluster.yml); load it with `--spring.config.additional-location=file:config/redis-cluster.yml` in addition to the redis profile.

## Configuration

- [application-elasticsearch.yml](src/main/resources/application-elasticsearch.yml): environment variable names and profile settings.
- [application-memcached.yml](src/main/resources/application-memcached.yml): environment variable names and profile settings.
- [application-mongodb.yml](src/main/resources/application-mongodb.yml): environment variable names and profile settings.
- [application-mysql.yml](src/main/resources/application-mysql.yml): environment variable names and profile settings.
- [application-postgresql.yml](src/main/resources/application-postgresql.yml): environment variable names and profile settings.
- [application-redis.yml](src/main/resources/application-redis.yml): environment variable names and profile settings.
- [application-tablestore.yml](src/main/resources/application-tablestore.yml): environment variable names and profile settings.

## Source and verification

- [MemoryBackends.java](src/main/java/io/github/agentic/ai/examples/MemoryBackends.java) is the primary entry point.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

Local builds and selected runtime checks were performed on 2026-10-15. See [validation results](../../docs/compatibility.md#verification-boundary) for the exact tested scope and remaining failures; compilation alone does not certify an external integration.

## Behavioral scope

- Profile-selected Redis, MongoDB, Elasticsearch, TableStore, Memcached, and JDBC chat repositories.
- Message-window eviction, a recreated facade, conversation isolation and targeted cleanup against the selected repository.

Limit: Backend profiles share one contract scenario. No backend connectivity, consistency, TTL or failure behavior has been verified.

## Source layout

| Source | Responsibility |
| --- | --- |
| [ConversationLifecycle.java](src/main/java/io/github/agentic/ai/examples/ConversationLifecycle.java) | Runs the same conversation lifecycle against the profile-selected repository. |
| [MemoryBackends.java](src/main/java/io/github/agentic/ai/examples/MemoryBackends.java) | Each Maven profile supplies exactly one real repository starter. |

The lifecycle report should retain Question 2 / Answer 2 / Question 3 / Answer 3, keep the other conversation isolated, and report successful targeted cleanup. Eventual-consistency backends may need caller-managed refresh before matching that expectation.
