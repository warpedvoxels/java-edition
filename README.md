<div align="center">
  <img src="./branding/logo-with-font.svg">
</div>

<sub>⚠️ **The Hexalite project is highly experimental, so bugs and possibly codebase and texture changes are expected. Contributions and bug tracking are welcome and gladly accepted. You can also join our [Discord community][discord] to interact with the art and development teams.**<sub>

<div align="center">
  <h3>
    <ins>Summary</ins>
  </h3>
  <strong>
    Hexalite Network is an open-source modular network of Minecraft: Java Edition servers inspired by Mineclub, Wynncraft and Origin Realms.
  </strong>
  This project aims to bring the best experience possible to players without the need of mods.
</div>

## 📚 Table of contents

* [💻 Technologies / Project stack](#-technologies)
* [✨ Contributors](#-contributors)
* [💸 Supporting](#-supporting)
* * [Starring the repository](#starring-the-repository)
* * [Donations](#donations)
* [🏟️ Contributing / Running locally](#-running-locally)
* * [Prerequisites](#prerequisites)
* * [Building the applications](#building-the-applications)
* * [Command-line tool explanation](#command-line-tool-explanation)
* [🏗️ Project structure](#-project-structure)
* [🎉 Third party](#-third-party)
* [📜 Licensing](#-licensing)

## 💻 Technologies

All of our Minecraft servers are built on top of the [Purpur][purpur] server software, proxied by [Velocity][velocity]. We also use [PostgreSQL][postgresql] as our database, and
use Redis for our multiserver caching and session system. We usually make a request to our rest webservers instead of directly accessing the database in multiple servers to avoid
unsynchronization or/and loss of data, and for communications we use [RabbitMQ][rabbitmq]. We also appreciate the work and we are very grateful to the various open source libraries used in the project which you can find [here][third-party].

## ✨ Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="http://www.exst.fun"><img src="https://avatars.githubusercontent.com/u/45243386?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Pedro Henrique</b></sub></a><br /><a href="https://github.com/playhexalite/java-edition/commits?author=eexsty" title="Code">💻</a> <a href="#infra-eexsty" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
    <td align="center"><a href="https://github.com/SrGaabriel"><img src="https://avatars.githubusercontent.com/u/58668092?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Gabriel</b></sub></a><br /><a href="https://github.com/playhexalite/java-edition/commits?author=SrGaabriel" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/santosbpd"><img src="https://avatars.githubusercontent.com/u/89719009?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Breno S.</b></sub></a><br /><a href="https://github.com/playhexalite/java-edition/commits?author=santosbpd" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/luissfx"><img src="https://avatars.githubusercontent.com/u/40919071?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Luis</b></sub></a><br /><a href="https://github.com/playhexalite/java-edition/commits?author=luissfx" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/herocrife"><img src="https://avatars.githubusercontent.com/u/59402242?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Herocrife</b></sub></a><br /><a href="#design-Herocrife" title="Design">🎨</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->


## 🏟️ Running locally

If you are interesting in contributing, please make sure to read our [contribution guide][contribution-guide] and join
our Discord community for further information and interaction with the developers and artists, and whoever knows what 
they are doing.

### Prerequisites

* Install the [Java Development Kit][jdk]. We recommend using the [IBM's Semeru][jdk-semeru] for the OpenJ9 virtual machine
and [Eclipse's Adoptium][jdk] for the Hotspot VM. We recommend installing both though.
* Install the latest nightly build of [Rust][rust].
* Install [Docker][docker] and [Docker Compose][docker-compose] for setting up the development environment in an easier way.
* Install ProtoBuf's `protoc` locally and add it to the $PATH.

### Building the applications

* **Compile the command-line interface first by going to the `cli` directory then running `cargo build --release`.** This will
generate a binary in `cli/target/release/hexalite` that will be referenced soon as `hexalite` for simplicity. You can add it
to the $PATH on UNIX-like environments by running the `scripts/apply_cli.sh` script.
* **Link the required components to the ~/.hexalite folder by running `hexalite init`.** This is required for getting all
server resources internally without any dumb workarounds.

### Command-line tool explanation

A explanation of every subcommand in the command-line tool can be obtained by running the `hexalite help` command.


## 🏗️ Project structure

This section will cover the project structure and the files that are used in the project.

* `arcade/*` - Every single game the Minecraft server has.
* `cli/*` - The command-line interface.
* `branding` - Assets related to the Hexalite branding.
* `common*` - Common files that are used by multiple components.
* `docker` - Compose files for Docker, for an easier setup of the development environment.
* `docs` - Documentation for features used in this project.
* `kraken` - A library for easier Minecraft development.
* `resource-pack` - The source code for the resource pack generator. Output is located at `resource-pack/out`.
* `rest-webserver` - The REST webserver for the server.
* `rest-webclient` - A consumer library for the REST webserver.
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


[rust]: https://www.rust-lang.org/

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
