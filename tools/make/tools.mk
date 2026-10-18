# Copyright 2024-2026 the original author or authors.
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

.PHONY: tools

# Keep the tools used by ARGI's checks isolated from the caller's environment.
tools:
	@test -x .git/ci-tools/venv/bin/python || python3 -m venv .git/ci-tools/venv
	.git/ci-tools/venv/bin/python -m pip install codespell==2.4.1 yamllint==1.35.1 PyYAML==6.0.2
	mkdir -p .git/ci-tools/bin
	GOBIN="$(CURDIR)/.git/ci-tools/bin" go install github.com/apache/skywalking-eyes/cmd/license-eye@v0.9.0
	GOBIN="$(CURDIR)/.git/ci-tools/bin" go install github.com/zricethezav/gitleaks/v8@v8.30.1
