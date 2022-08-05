# 🏟️ Getting Started

If you are interested in contributing, please make sure to read our [contributing guide] and join our Discord community 
for further information and interaction with the developers and artists, and whoever knows what they are doing.

If you are running a Windows machine, we recommend using [WSL2], since it has better tooling for Rust, and it won't give
you headaches with a lot of issues you would have normally, besides being a lot easier to use with Docker as well, which
is another required tool. You can find a guide in how to set up WSL2 [here][wsl2-setup].

[WSL2]: https://docs.microsoft.com/en-us/windows/wsl/install
[wsl2-setup]: https://docs.microsoft.com/en-us/windows/wsl/install#install-wsl-command
[contributing guide]: https://git.hexalite.org/java-edition/blob/dev/next/CONTRIBUTING.md

## Installing the development environment

### 1. Cloning the Git repository

First of all, we need to get the source code from Git:
```bash
git clone https://git.hexalite.org/java-edition hexalite && cd hexalite
```

### 2. Installing the required stuff

We have an installer script that will make you avoid wasting a lot of time with this step. You just need Python 3.10 or
higher to run it.
```bash
cd installer && python3 ./main.py
```

Now we need to install the things that the script cannot install for you:
* [Docker] and [docker-compose] - They may often be found in your package manager of choice
* [Java 17] - This is an optional step. You need to install it just if you are having issues with Gradle
* [mold] (only if on Linux) - A modern drop-in replacement for existing Linux linkers
* [zld] (only if on macOS) - A faster version of Apple's linker
* [MSVC] (only if on Windows and not running WSL2) - The required build tools for running Rust outside WSL2

[Docker]: https://www.docker.com/
[docker-compose]: https://docs.docker.com/compose/
[Java 17]: https://adoptium.net/
[mold]: https://github.com/rui314/mold
[zld]: https://github.com/michaeleisel/zld
[MSVC]: https://docs.microsoft.com/en-us/windows/dev-environment/rust/setup

## 3. Compiling

First of all, if you are an IntelliJ IDEA user, make sure to ignore the following directories to allow a faster
indexing step:
* `**/**/target`
* `/target/`
* `/web/node_modules`

### Compiling the command-line interface

The command-line interface is required to set up all environment-related stuff, such as symbolic links, easier building,
running stuff, and a bunch of upcoming things.
```bash
cargo build -p hexalite --release # it needs to be added to the path manually
# or
# you can run this if you are running a linux-based machine and have ~/.bin added to the path: ./scripts/apply_cli.sh
```

The `apply_cli.sh` script will take care of everything else you need to run related to the CLI, but if you didn't compiled
using it, you can run this commands below:
```
# make sure to replace 'hexalite' with the path of the binary if it is not added to the path
hexalite init . # if you aren't in the hexalite's source code root folder, make sure to replace '.' with its path
```

### Compiling everything else

You can now use `hexalite build` to compile everything else. If you are planning to only compile Kotlin-based modules,
you can run the `build` subcommand through `gradlew` or `gradlew.bat`.


## 👌 Developer resources

* [wiki.vg](https://wiki.vg) - Addresses documentation about the Minecraft: Java Edition protocol.
* [Prisma documentation](https://prisma.io/docs/prisma-client/introduction) - Addresses documentation about the Prisma.
  ORM.
* [Prisma Client Rust documentation](https://github.com/Brendonovich/prisma-client-rust/tree/main/docs).
* [The Rust Book](https://doc.rust-lang.org/book/) - Book to learn how the Rust programming language works.
* [The Kotlin programming language reference](https://kotlinlang.org/docs/reference/) - Reference about the Kotlin.
  programming language.


## 🏗️ Project structure

This section will cover the project structure and the files that are used in the project. I think everything else that
is not explained below is self-explanatory.

### `arcade`

Every single game our Minecraft: Java Edition network of server has. All modules there are Kotlin-based and need a
`DEPENDENCIES` file containing the name of all modules this game depends on, which it is handled by the command-line
interface when building with `hexalite build arcade-module-name-here`.

### `kraken`

A library that allows easier Minecraft-related development.

### `resource-pack`

The source code for the resource pack generator.

## `common-(kotlin|rust)`

Common code used by modules of the same programming language this module was written in.

## `grpc-server`

The gRPC server for interacting with the database and internal stuff.

## `rest-server`

A public and limited version of our gRPC server available as a REST server.

## `kotlin-grpc-client`

A consumer library written in Kotlin for our gRPC server.

## `js-rest-client`

A consumer library written in TypeScript for our REST server.

## `reusable-plugins`

A collection of reusable plugins for our Minecraft: Java Edition servers.

## `run`

A development environment for a Purpur setup.

