#!/bin/bash

DIRNAME="$(dirname "$(realpath "$0")")/.."
cargo build --release --manifest-path "$DIRNAME/Cargo.toml"
"$DIRNAME/target/release/hexalite" init "$DIRNAME"
"$DIRNAME/target/release/hexalite" build
mkdir -p "$HOME/.bin/" 
ln -sf "$HOME/.hexalite/compiled/hexalite" "$HOME/.bin/"
