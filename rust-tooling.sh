#!/bin/sh
DIRNAME=$(dirname "$(realpath "$0")")
(cd "$DIRNAME" && cargo run -p jvm-definitions-generator && cargo run -p hexalite-lint-tool)