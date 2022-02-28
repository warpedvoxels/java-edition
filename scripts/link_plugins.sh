#!/bin/bash

DIRNAME=$(dirname "$(realpath "$0")")
cd "$DIRNAME/.."
shopt -s dotglob

# Check if the first argument is not empty
if [ -z "$1" ]; then
  echo "No argument supplied. Please provide one of the arcade games as an argument."
  exit 1
fi

# Check if the 'run' directory exists
if [ ! -d "./run" ]; then
  echo "The 'run' directory does not exist. Please run the 'download_purpur.sh' script first."
  exit 1
fi
mkdir -p ./run/plugins

# This is self-explanatory.
# * purpur | waterfall
SERVER_TYPE="purpur"

# Check if the first argument is a valid choice
# * duels | origins
case $1 in
  duels|origins)
    GAME="$1"
    ;;
  *)
    echo "Invalid argument supplied. Please provide one of the arcade games as an argument."
    exit -1
    ;;
esac

# Remove unnecessary plugins
rm -rf "$PWD/run/plugins/"*-reobf.jar

# Finally, link the plugins to their respective files
case $SERVER_TYPE in
    purpur)
        if [ ! -f "./arcade/$GAME/DEPENDENCIES" ]; then
            echo "The 'arcade/$GAME' directory does not contain a DEPENDENCIES file, that means that this module's recyclable dependencies would not be linked."
            continue 
        else
            for dependency in `cat "./arcade/$GAME/DEPENDENCIES"`; do
                if [ ! -d "./$dependency" ]; then
                    echo "The '$dependency' dependency does not exist."
                    continue
                fi
                echo "$PWD"
                ln -nfs "$PWD/$dependency/build/libs/$(basename "$dependency")-reobf.jar" "$PWD/run/plugins/"
            done
        fi
        echo "=> Linking arcade plugin for '$GAME'"
        ln -nfs "$PWD/arcade/$GAME/build/libs/$GAME-reobf.jar" "$PWD/run/plugins/"
        ln -nfs "$PWD/kraken/purpur/build/libs/purpur-reobf.jar" "$PWD/run/plugins/"
        ;;
    waterfall|flamecord)
        echo "Not yet supported."
        exit -1
        ;;
    *)
        echo "Invalid server type supplied"
        exit -1
        ;;
esac
