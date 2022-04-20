#!/bin/bash

DIRNAME=$(dirname "$(realpath "$0")")
cargo build --manifest-path "$DIRNAME/../cli/Cargo.toml"
sudo ln -sf "$DIRNAME/../cli/target/release/hexalite" /usr/bin/
