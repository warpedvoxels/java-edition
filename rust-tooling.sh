#!/bin/sh
DIRNAME=$(dirname "$(realpath "$0")")
(cd "$DIRNAME" && cargo run -p hexalite-lint-tool)