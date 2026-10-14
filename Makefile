.PHONY: catalog bootstrap compile verify install

catalog:
	python3 tools/check_catalog.py

bootstrap:
	bash tools/bootstrap.sh

compile:
	bash tools/mvn.sh -B -Dmaven.test.skip=true package

verify:
	bash tools/mvn.sh -B verify

install:
	bash tools/mvn.sh -B -Dmaven.test.skip=true install
