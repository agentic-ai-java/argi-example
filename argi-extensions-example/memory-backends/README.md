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

The runner saves two messages under a unique conversation ID, reads them through the selected repository, and clears only that conversation. No in-memory fallback is provided. Omitting the backend profile fails to supply the repository bean. The Redis cluster/TLS variant is in [redis-cluster.yml](config/redis-cluster.yml); load it with `--spring.config.additional-location=file:config/redis-cluster.yml` in addition to the redis profile.

## Configuration

- [application-elasticsearch.yml](src/main/resources/application-elasticsearch.yml): environment variable names and profile settings.
- [application-memcached.yml](src/main/resources/application-memcached.yml): environment variable names and profile settings.
- [application-mongodb.yml](src/main/resources/application-mongodb.yml): environment variable names and profile settings.
- [application-mysql.yml](src/main/resources/application-mysql.yml): environment variable names and profile settings.
- [application-postgresql.yml](src/main/resources/application-postgresql.yml): environment variable names and profile settings.
- [application-redis.yml](src/main/resources/application-redis.yml): environment variable names and profile settings.
- [application-tablestore.yml](src/main/resources/application-tablestore.yml): environment variable names and profile settings.

## Source and verification

- [MemoryBackends.java](src/main/java/io/github/agentic/ai/examples/MemoryBackends.java) contains the complete example.
- [Website coverage](../../docs/website-coverage.md) maps documentation chapters to examples.
- [Compatibility notes](../../docs/compatibility.md) describe RC2 API changes and the verification boundary.

This expanded repository was reviewed statically only after the user requested no execution. No build, runtime, service-integration, or coverage result is claimed for the final version.
