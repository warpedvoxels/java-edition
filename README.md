<div align="center">
  <img src="./branding/logo-with-font.svg" />
  <br/> <br/>
  <a href="https://discord.hexalite.org">
    <img src="https://img.shields.io/discord/908438033613848596?colorA=1e1e28&colorB=1187c9&style=for-the-badge&logo=discord" />
  </a>
  <a href="https://git.hexalite.org/java-edition">
    <img src="https://img.shields.io/github/stars/playhexalite/java-edition?colorA=1e1e28&colorB=1187c9&style=for-the-badge&logo=github">
  </a>
  <a href="https:/git.hexalite.org/java-edition/actions/workflows/cargo.yml">
    <img src="https://img.shields.io/github/workflow/status/playhexalite/java-edition/Rust%20CI%20with%20Cargo?colorA=1e1e28&colorB=1187c9&label=Rust&style=for-the-badge&logo=rust">
  </a>
  <a href="https:/git.hexalite.org/java-edition/actions/workflows/gradle.yml">
    <img src="https://img.shields.io/github/workflow/status/playhexalite/java-edition/Kotlin%20CI%20with%20Gradle?colorA=1e1e28&colorB=1187c9&label=Kotlin&style=for-the-badge&logo=kotlin">
  </a>
</div>

<div align="center">
  <br/>
  <strong>
    Hexalite Network is an open-source modular network of Minecraft: Java Edition servers inspired by Mineclub, Wynncraft and Origin Realms.
  </strong>
  This project aims to bring the best experience possible to players without the need of mods.
  <br/>
</div>

<br/>


## 📚 Table of contents

1. [💻 Technologies / Project stack](#-technologies)
2. [✨ Contributors](#-contributors)
3. [💸 Supporting](#-supporting)
   * [Starring the repository](#starring-the-repository)
   * [Donations](#donations)
4. [🏟️ Contributing / Running locally](#%EF%B8%8F-contributing--running-locally)
   * [Prerequisites](#prerequisites)
   * [Building everything](#building-everything)
     * [1. Compiling the command-line interface](#1-compiling-the-command-line-interface)
     * [2. Initializing the environment](#2-initializing-the-environment)
5. [🏗️ Project structure](#%EF%B8%8F-project-structure)
6. [🎉 Third party](#-third-party)
7. [📜 Licensing](#-licensing)


## 💻 Technologies

The technology stack in this project is pretty straightforward and consists mainly of [Rust][rust] and [Kotlin][kotlin] when talking about programming languages. For transferring data between modules, we use a gRPC server based on `prost` over the CBOR binary format and compressed data. It is used for requesting data, and communicating between other stuff with `RabbitMQ`. Every Minecraft server on Hexalite is running on top of [Purpur][purpur] server software, proxied by [Velocity][velocity]. Purpur is a great alternative for Paper and it provide a lot of new features, such as mechanics, performance improvements and useful API changes.

This is subject to change, since this type of infrastructure can improve over time. If you are interested in discussing about this or any other
technology topics, feel free to join our [Discord server][discord] to chat with our development and artistic team.

## ✨ Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="http://www.exst.fun"><img src="https://avatars.githubusercontent.com/u/45243386?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Pedro Henrique</b></sub></a><br /><a href="https://github.com/playhexalite/java-edition/commits?author=eexsty" title="Code">💻</a> <a href="#infra-eexsty" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/playhexalite/java-edition/pulls?q=is%3Apr+reviewed-by%3Aeexsty" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/playhexalite/java-edition/commits?author=eexsty" title="Documentation">📖</a> <a href="#translation-eexsty" title="Translation">🌍</a></td>
    <td align="center"><a href="https://github.com/SrGaabriel"><img src="https://avatars.githubusercontent.com/u/58668092?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Gabriel</b></sub></a><br /><a href="https://github.com/playhexalite/java-edition/commits?author=SrGaabriel" title="Code">💻</a> <a href="#translation-SrGaabriel" title="Translation">🌍</a></td>
    <td align="center"><a href="https://github.com/santosbpd"><img src="https://avatars.githubusercontent.com/u/89719009?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Breno S.</b></sub></a><br /><a href="https://github.com/playhexalite/java-edition/commits?author=santosbpd" title="Code">💻</a> <a href="#translation-santosbpd" title="Translation">🌍</a></td>
    <td align="center"><a href="https://github.com/luissfx"><img src="https://avatars.githubusercontent.com/u/40919071?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Luis</b></sub></a><br /><a href="https://github.com/playhexalite/java-edition/commits?author=luissfx" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/herocrife"><img src="https://avatars.githubusercontent.com/u/59402242?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Herocrife</b></sub></a><br /><a href="#design-Herocrife" title="Design">🎨</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->


## 🏟️ Contributing / running locally

If you are interesting in contributing, please make sure to read our [contributing guide][contributing-guide] and join
our Discord community for further information and interaction with the developers and artists, and whoever knows what 
they are doing.

### Prerequisites

* Install the [Java Development Kit][jdk]. We recommend using the [IBM's Semeru][jdk-semeru] for the OpenJ9 virtual machine
and [Eclipse's Adoptium][jdk] for the Hotspot virtual machine. We recommend installing both though.
* Install the latest nightly build of [Rust][rust]. You may consider using the `rustup` tool to make your life easier. 
* Install [Docker][docker] and [Docker Compose][docker-compose] for setting up the development environment in an easier way.

The reason why we recommend installing both, is it because the OpenJ9 virtual machine is known to use less memory and CPU than Hotspot, so it is a better choice for running Minecraft servers. In contrast to this, the hotspot virtual machine is known to be more stable and more efficient in terms of IDE support, so it is a better choice for developing.

### Building everything

#### 1. Compiling the command-line interface

The command-line interface is a tool written in Rust highly used in both development and productivity environments on Hexalite.
It is a unified interface for doing all necessary things for running Hexalite.

* You can start by cloning the repository, which needs [`Git`][git] to be installed in your system: `git clone https://git.hexalite.org/java-edition hexalite-java-edition`.
* Then you can compile all the Rust-based applications by running `cd hexalite-java-edition && cargo build --release`.

It is done now! You can link the compiled binaries to the `$PATH` by running the `./scripts/apply_cli.sh` command on UNIX-based systems. I'm
not too sure in how to do this on Windows, but I'm sure you can find some help there by Googling it, since it doesn't seems that hard.

#### 2. Initializing the environment

After the compilation of the command-line interface, we need to initialize the environment. In this document, we will be referring to the
command-line interface command as `hexalite`. It may different in your machine if you didn't added it to the `$PATH`, so it probably it would be
`./target/release/hexalite`, assuming you already compiled it.

* `hexalite init` ◉ This will initialize the environment, creating the `.hexalite` folder in your home directory, and symlinking all required
files to this folder. You can get further explanation about how the command-line interface works by running `hexalite help`.

## 🏗️ Project structure

This section will cover the project structure and the files that are used in the project.

* `arcade/*` - Every single game the Minecraft server has.
* `cli/*` - The command-line interface.
* `branding` - Assets related to the Hexalite branding.
* `common-*` - Common code used in multiple modules.
* `docker` - Compose files for Docker, for an easier setup of the development environment.
* `docs` - Documentation for features used in this project.
* `kraken` - A library for easier Minecraft development.
* `resource-pack` - The source code for the resource pack generator. Output is located at `resource-pack/out`.
* `grpc-server` - The gRPC server for interacting with the database other stuff.
* `rest-server` - A public version of our gRPC server available as a REST server.
* `kotlin-grpc-client` - A consumer library for our gRPC server.
* `js-rest-client` - A consumer library for our REST server.
* `reusable-plugins` - A collection of reusable plugins for the server.
* `web` - The web interface for the server.
* `run` - A development environment for a Purpur setup.


## 💸 Supporting

### Starring the repository

If you do not want to help us with money, you can just star the repository, it still means a lot to us!

### Donations

You can support us by the GitHub sponsors program (**pending**) or on [OpenCollective][opencollective].


## 🎉 Third party

We depend on many third party libraries and applications, a complete list can be found [here][third-party].


## 📜 Licensing

To know about the license of this project, you can read the [LICENSE.md][license] file.


[contributing-guide]: https://git.hexalite.org/java-edition/blob/dev/next/CONTRIBUTING.md

[rust]: https://www.rust-lang.org/

[kotlin]: https://kotlinlang.org/

[git]: https://git-scm.com/

[docker]: https://www.docker.com/

[docker-compose]: https://docs.docker.com/compose/

[jdk]: https://projects.eclipse.org/projects/adoptium.temurin

[jdk-semeru]: https://developer.ibm.com/languages/java/semeru-runtimes/downloads

[opencollective]: https://opencollective.com/hexalite

[third-party]: https://git.hexalite.org/java-edition-network/blob/dev/next/THIRD_PARTY.md

[license]: https://git.hexalite.org/java-edition-network/blob/dev/next/LICENSE.md

[purpur]: https://purpurmc.org

[velocity]: https://github.com/PaperMC/Velocity

[rabbitmq]: https://www.rabbitmq.com

[postgresql]: https://www.postgresql.org

[discord]: https://discord.hexalite.org

