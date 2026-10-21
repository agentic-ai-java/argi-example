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

.PHONY: catalog bootstrap compile format format-fix format-check checkstyle-check verify install

SHELL := /bin/bash
LOG_TARGET = true
export PATH := $(CURDIR)/.git/ci-tools/venv/bin:$(CURDIR)/.git/ci-tools/bin:$(PATH)

catalog:
	python3 tools/check_catalog.py

bootstrap:
	bash tools/bootstrap.sh

compile:
	bash tools/mvn.sh -B -Dmaven.test.skip=true package

format:
	bash tools/mvn.sh -B -Plegacy-agentscope spring-javaformat:apply

format-fix: format

format-check:
	bash tools/mvn.sh -B -Plegacy-agentscope spring-javaformat:validate

checkstyle-check:
	bash tools/mvn.sh -B -Plegacy-agentscope -Dcheckstyle.skip=false checkstyle:check

verify: catalog format-check checkstyle-check compile

install:
	bash tools/mvn.sh -B -Dmaven.test.skip=true install

include tools/make/linter.mk
include tools/make/tools.mk
