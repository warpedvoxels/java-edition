#!/bin/bash

DIRNAME=$(dirname "$(realpath "$0")")
cd "$DIRNAME/.."

mkdir -p "$HOME/.hexalite"
if [ ! -L "$HOME/.hexalite/resource_pack" ]; then
    ln -nfs "$PWD/resource_pack" "$HOME/.hexalite/resource_pack"
fi
if [ ! -L "$HOME/.hexalite/dev" ]; then
    ln -nfs "$PWD" "$HOME/.hexalite/dev"
fi
if [ ! -L "$HOME/.hexalite/minecraft-testing" ]; then
    ln -nfs "$PWD/run" "$HOME/.hexalite/minecraft-testing"
fi
ln -fs "$PWD/.env" "$HOME/.hexalite/.env"