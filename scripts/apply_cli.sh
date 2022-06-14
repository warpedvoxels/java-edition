#!/bin/bash

DIRNAME="$(dirname "$(realpath "$0")")/.."
cargo build --release --manifest-path "$DIRNAME/Cargo.toml"
"$DIRNAME/target/release/hexalite" init "$DIRNAME"
"$DIRNAME/target/release/hexalite" build
sudo ln -sf "$HOME/.hexalite/compiled/hexalite" /usr/bin/
