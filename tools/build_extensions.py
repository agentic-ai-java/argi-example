#!/usr/bin/env python3
"""Build the pinned Extensions source against RC2 without editing its checkout."""

from pathlib import Path
import shutil
import subprocess
import sys
import tempfile


def main():
    if len(sys.argv) < 3:
        raise SystemExit("Usage: build_extensions.py <source> <Maven arguments...>")
    root = Path(__file__).resolve().parents[1]
    source = Path(sys.argv[1]).resolve()
    scratch = root / "target" / "bootstrap"
    scratch.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix="extensions-", dir=scratch) as temporary:
        staged = Path(temporary)
        shutil.copytree(source, staged, dirs_exist_ok=True,
                        ignore=shutil.ignore_patterns(".git", "target", ".DS_Store"))
        executor = staged / "starters/argi-starter-a2a-nacos/src/main/java/io/github/agentic/ai/a2a/core/server/GraphAgentExecutor.java"
        content = executor.read_text()
        previous = "((StreamingOutput) nodeOutput).chunk()"
        replacement = "((StreamingOutput<?>) nodeOutput).text()"
        if previous in content:
            executor.write_text(content.replace(previous, replacement))
            print("Applied the pinned Extensions A2A streaming compatibility fix in a temporary copy.", flush=True)
        elif replacement not in content:
            raise SystemExit("Unrecognized Extensions A2A streaming implementation; review the RC2 compatibility fix")
        # Core RC2 no longer supplies provider SDKs transitively. Sandbox tools still
        # implement Spring AI ToolCallback, so that module must declare its own SDK.
        sandbox_pom = staged / "sandbox/argi-sandbox/pom.xml"
        sandbox_xml = sandbox_pom.read_text()
        if "<artifactId>spring-ai-model</artifactId>" not in sandbox_xml:
            dependency = """
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-model</artifactId>
        </dependency>
"""
            sandbox_pom.write_text(sandbox_xml.replace("    <dependencies>", "    <dependencies>" + dependency, 1))
            print("Declared the Sandbox Spring AI SDK dependency in the temporary copy.", flush=True)
        command = ["bash", str(root / "tools/mvn.sh"), "-f", str(staged / "pom.xml"), *sys.argv[2:]]
        return subprocess.run(command, cwd=root, check=False).returncode


if __name__ == "__main__":
    sys.exit(main())
