#!/bin/bash

DIRNAME=$(dirname "$(realpath "$0")")
cd "$DIRNAME/../run"

if [ -f "./purpur.jar" ]; then
  if [ "$1" == "-f"]; then
    rm -f purpur.jar
  else
    echo "Could not overwrite purpur.jar. Use -f to force overwrite."
    exit -1
  fi
fi
curl https://api.purpurmc.org/v2/purpur/1.18.2/latest/download -o ./purpur.jar
