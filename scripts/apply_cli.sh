#!/bin/bash

DIRNAME=$(dirname "$(realpath "$0")")
cargo build --manifest-path "$DIRNAME/../Cargo.toml"
sudo ln -sf "$HOME/.hexalite/compiled/hexalite" /usr/bin/
