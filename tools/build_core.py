#!/usr/bin/env python3
# Copyright 2025-2026 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Build the pinned Core source with the nested-stream terminal-event correction."""

from pathlib import Path
import shutil
import subprocess
import sys
import tempfile


def main():
    if len(sys.argv) < 3:
        raise SystemExit("Usage: build_core.py <source> <Maven arguments...>")
    root = Path(__file__).resolve().parents[1]
    source = Path(sys.argv[1]).resolve()
    scratch = root / "target" / "bootstrap"
    scratch.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix="core-", dir=scratch) as temporary:
        staged = Path(temporary)
        shutil.copytree(source, staged, dirs_exist_ok=True,
                        ignore=shutil.ignore_patterns(".git", "target", ".DS_Store"))
        executor = staged / "argi-graph-core/src/main/java/io/github/agentic/ai/graph/executor/NodeExecutor.java"
        content = executor.read_text()
        previous = "if (chatResponse.messages().isEmpty()) {"
        replacement = "if (chatResponse.messages().isEmpty() || streaming.messages().isEmpty()) {"
        if content.count(previous) == 1:
            executor.write_text(content.replace(previous, replacement, 1))
            print("Applied the pinned Core nested-stream terminal-event fix in a temporary copy.", flush=True)
        elif replacement not in content:
            raise SystemExit("Unrecognized Core terminal-event filter; review the compatibility correction")
        command = ["bash", str(root / "tools/mvn.sh"), "-f", str(staged / "pom.xml"), *sys.argv[2:]]
        return subprocess.run(command, cwd=root, check=False).returncode


if __name__ == "__main__":
    sys.exit(main())
