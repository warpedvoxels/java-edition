#!/bin/sh
DIRNAME=$(dirname "$(realpath "$0")")
(CD "$DIRNAME" && cargo run -p jvm-definitions-generator && cargo run -p hexalite-lint-tool && cbindgen --config grpc-server/bindings/cbindgen.toml --crate grpc-server-bindings --output target/release/client.h)