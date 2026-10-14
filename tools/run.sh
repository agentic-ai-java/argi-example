#!/usr/bin/env bash
set -euo pipefail
repo_root="$(cd -- "$(dirname -- "$0")/.." && pwd)"
if [ "$#" -lt 1 ]; then
  printf 'Usage: bash tools/run.sh <group/example> [Maven arguments]\n' >&2
  exit 2
fi
module="$1"
shift
case "$module" in
  argi-graph-example/*|argi-agent-example/*|argi-adapter-example/*|argi-extensions-example/*) ;;
  *) printf 'Unknown example group: %s\n' "$module" >&2; exit 2 ;;
esac
case "$module" in *..*|/*) printf 'Use a repository-relative module path\n' >&2; exit 2 ;; esac
test -f "$repo_root/$module/pom.xml" || { printf 'Unknown example: %s\n' "$module" >&2; exit 2; }
# The caller explicitly selects the example. External services must already exist.
cd "$repo_root/$module"
exec bash "$repo_root/tools/mvn.sh" exec:java "$@"
