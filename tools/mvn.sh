#!/usr/bin/env bash
set -euo pipefail
# Use installed, pinned toolchains. This wrapper never installs tools.
if command -v mise >/dev/null 2>&1; then
  exec mise exec java@temurin-17.0.19+10 maven@3.9.10 -- mvn "$@"
fi
exec mvn "$@"
