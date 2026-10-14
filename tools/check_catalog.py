#!/usr/bin/env python3
"""Check source/document wiring without compiling or executing any example."""

import json
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
NS = {"m": "http://maven.apache.org/POM/4.0.0"}


def check():
    catalog = json.loads((ROOT / "docs/catalog.json").read_text())
    errors = []
    actual = {str(p.parent.relative_to(ROOT)) for p in ROOT.glob("argi-*-example/*/pom.xml")}
    listed = {module["path"] for module in catalog["modules"]}
    if actual != listed:
        errors.append(f"Catalog/module mismatch: {actual ^ listed}")
    for pom in ROOT.glob("**/pom.xml"):
        if "target" in pom.parts:
            continue
        tree = ET.parse(pom)
        for module in tree.findall("m:modules/m:module", NS):
            if not (pom.parent / module.text / "pom.xml").is_file():
                errors.append(f"Missing Maven module: {pom}: {module.text}")
        for dependency in tree.findall(".//m:dependency", NS):
            group = dependency.findtext("m:groupId", namespaces=NS)
            artifact = dependency.findtext("m:artifactId", namespaces=NS)
            if group == "io.github.agentic-ai-java.examples" and artifact not in {m["artifact"] for m in catalog["modules"]}:
                errors.append(f"Unknown example dependency: {artifact}")
    for module in catalog["modules"]:
        directory = ROOT / module["path"]
        for required in ["pom.xml", "README.md", module["entry"]]:
            if not (directory / required).is_file():
                errors.append(f"Missing file: {directory / required}")
        source = (directory / module["entry"]).read_text()
        if "public static void main(String[] args)" not in source:
            errors.append(f"Missing main method: {module['path']}")
        for profile in module["profiles"]:
            backend = profile.split("-", 1)[1]
            if not (directory / f"src/main/resources/application-{backend}.yml").is_file():
                errors.append(f"Missing profile configuration: {module['path']}: {profile}")
    for mapping in catalog["websiteCoverage"]:
        for example in mapping["examples"]:
            if example not in listed:
                errors.append(f"Unknown coverage target: {example}")
    # When sibling sources exist, also verify every website framework chapter is mapped.
    website = ROOT.parent / "website/docs/frameworks"
    if website.is_dir():
        chapters = {"website/docs/frameworks/" + str(p.relative_to(website)) for p in website.rglob("*.md")}
        mapped = {mapping["source"] for mapping in catalog["websiteCoverage"]}
        if chapters - mapped:
            errors.append(f"Unmapped website chapters: {sorted(chapters - mapped)}")
    for path in ROOT.rglob("*.md"):
        if ".git" in path.parts or "target" in path.parts:
            continue
        for link in re.findall(r"\[[^\]]*\]\(([^)]+)\)", path.read_text()):
            if "://" in link or link.startswith("#"):
                continue
            target = link.split("#", 1)[0]
            if target and not (path.parent / target).exists():
                errors.append(f"Broken local link: {path.relative_to(ROOT)} -> {target}")
    for path in ROOT.rglob("*.java"):
        if "target" in path.parts:
            continue
        content = path.read_text()
        if "io.github.agentic.spring.ai" in content:
            errors.append(f"Legacy website package in {path.relative_to(ROOT)}")
        if re.search(r"NotImplemented|TODO|FIXME", content):
            errors.append(f"Unfinished source marker in {path.relative_to(ROOT)}")
    if errors:
        print("\n".join(errors), file=sys.stderr)
        return 1
    print(f"Catalog OK: {len(listed)} modules; {len(catalog['websiteCoverage'])} website mappings; Maven and local links resolved.")
    print("This is a static source check, not a build or runtime test.")
    return 0


if __name__ == "__main__":
    sys.exit(check())
