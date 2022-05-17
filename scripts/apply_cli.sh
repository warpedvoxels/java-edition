#!/bin/bash

DIRNAME=$(dirname "$(realpath "$0")")
cargo build --manifest-path "$DIRNAME/../Cargo.toml"
sudo ln -sf "$DIRNAME/../target/release/hexalite" /usr/bin/
