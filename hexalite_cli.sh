#!/bin/bash

REALPATH=$(realpath "$0")
DIRNAME=$(dirname "$REALPATH")
(cd "$DIRNAME" && chmod +x ./scripts/* && ./scripts/link_home.sh)

GRADLE_VERSION=$(gradle --version 2>/dev/null | sed -n 's/^.*Gradle \(.*\)/\1/p' || echo "Not found")
KOTLIN_VERSION=$(kotlin -version 2>/dev/null | sed -n 's/^.*Kotlin version \(.*\)/\1/p' || echo "Not found")

run_script() {
  (cd "$DIRNAME" && bash "./scripts/$1.sh" "$2")
}

cli_help() {
      echo "Hexalite Network CLI (at $0) v0.1.0 Copyright © 2021-2022 Hexalite Studios"
      echo "Versions"
      echo "  Gradle: $GRADLE_VERSION"
      echo "  Kotlin: $KOTLIN_VERSION"
      echo ""
      echo "Usage: $0 <option>"
      echo "Basic options:"
      echo "  -h, --help: Show this help"
      echo "  -b, --build: Build the webserver and all plugins and symlink to their respective directories"
      echo "  -p, --purpur: Install the Minecraft server environment using Purpur"
      echo "  -d, --docker: Compose the Docker environment"
      echo "  -s, --symlink: Symlink the CLI to the /usr/bin directory"
      echo "  -m, --minecraft: Start the testing Minecraft server with only 1GB of RAM"
      echo "  -w, --webserver: Run the compiled webserver"
      echo "  -r, --resource-pack: Run the Kotlin-based resource pack generator"
}

cli_build() {
  # Check if Gradle is installed
  if [ "$GRADLE_VERSION" == "Not found" ]; then
    echo "Gradle is not installed. Please install Gradle and try again."
    exit 1
  fi
  # Check if Kotlin is installed
  if [ "$KOTLIN_VERSION" == "Not found" ]; then
    echo "Kotlin is not installed. Please install Kotlin and try again."
    exit 1
  fi

  # Build everything then symlink everything
  mkdir -p "$HOME/.hexalite/compiled"
  if [ -z "$1" ]; then
    (cd "$DIRNAME" && gradle build && ln -sf "$DIRNAME/rest-webserver/build/libs/rest-webserver-shaded.jar" "$HOME/.hexalite/compiled/webserver.jar" && ln -sf "$DIRNAME/resource-pack-generator/build/libs/rp-shaded.jar" "$HOME/.hexalite/compiled/resource-pack-generator.jar")
  else
    (cd "$DIRNAME" && gradle build && run_script "link_plugins" "$1" && ln -sf "$DIRNAME/rest-webserver/build/libs/*-all.jar" "$HOME/.hexalite/compiled/webserver.jar"  && ln -sf "$DIRNAME/resource-pack-generator/build/libs/rp-shaded.jar" "$HOME/.hexalite/compiled/resource-pack-generator.jar")
  fi
}

cli_purpur() {
  # Make sure curl is installed
  if ! [ -x "$(command -v curl)" ]; then
    echo "curl is not installed. Please install curl and try again."
    exit 1
  fi
  run_script download_purpur
}

cli_docker() {
  # Make sure docker-compose is installed
  if ! [ -x "$(command -v docker-compose)" ]; then
    echo "docker-compose is not installed. Please install docker-compose and try again."
    exit 1
  fi
  run_script docker
}

cli_symlink() {
  echo "Linking '$REALPATH' to /usr/bin/hexalite"
  sudo ln -s "$REALPATH" /usr/bin/hexalite
}

cli_minecraft() {
  if [ "$1" != "hotspot" ]; then
    (cd "$DIRNAME/run" && ./start)
  else
    (cd "$DIRNAME/run" && ./start-hotspot)
  fi
}

cli_webserver() {
  (cd "$HOME/.hexalite/compiled" && java -jar ./webserver.jar)
}

cli_resource_pack() {
  (cd "$HOME/.hexalite/compiled" && java -jar ./resource-pack-generator.jar)
}

if [ -z "$1" ]; then
    cli_help
    exit 1
fi

case "$1" in
  -h|--help)
    cli_help
    ;;
  -b|--build)
    cli_build "$2"
    ;;
  -p|--purpur)
    cli_purpur
    ;;
  -d|--docker)
    cli_docker
    ;;
  -s|--symlink)
    cli_symlink
    ;;
  -m|--minecraft)
    cli_minecraft "$2"
    ;;
  -w|--webserver)
    cli_webserver
    ;;
  -r|--resource-pack)
    cli_resource_pack
    ;;
  *)
    cli_help
    ;;
esac
