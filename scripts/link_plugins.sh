#!/bin/bash

cd "${0%/*}"
cd ..

# Check if the first argument is not empty
if [ -z "$1" ]; then
  echo "No argument supplied"
  exit 1
fi

# Check if the 'run' directory exists
if [ ! -d "./run" ]; then
  echo "The 'run' directory does not exist. Please run the 'download_purpur.sh' script first."
  exit 1
fi

# Check if the first argument is a valid choice
# * duels | origins
case $1 in
  duels)
    TARGETS=("arcade/duels/build")
  origins)
    TARGETS=("arcade/origins/skills/build")
  *)
    echo "Invalid argument supplied"
    exit 1
    ;;
esac

# Remove unnecessary plugins
rm -rf ./run/plugins/*-reobf.jar

# Finally, link the plugins to their respective files
for build_dir in ${TARGETS[0]}; do
  ln -s "./$build_dir/*-reobf.jar" ./run/plugins/
done
