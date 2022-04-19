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
* [✨ Contributing / Running locally](#-running-locally)
* * [UNIX-based systems](#unix-based-systems)
* * [Windows](#windows)
* [🎉 Third party](#-third-party)
* [📜 Licensing](#-licensing)

## 💻 Technologies

All of our Minecraft servers are built on top of the [Purpur][purpur] server software, proxied by [Velocity][velocity]. We also use [PostgreSQL][postgresql] as our database, and
use Redis for our multiserver caching and session system. We usually make a request to our rest webservers instead of directly accessing the database in multiple servers to avoid
unsynchronized or/and loss of data, and for communications we use [RabbitMQ][rabbitmq].

You can view a notice file of shaded libraries [here](shaded).

## ✨ Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="http://www.exst.fun"><img src="https://avatars.githubusercontent.com/u/45243386?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Pedro Henrique</b></sub></a><br /><a href="https://github.com/HexaliteNetwork/java-edition/commits?author=eexsty" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/SrGaabriel"><img src="https://avatars.githubusercontent.com/u/58668092?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Gabriel</b></sub></a><br /><a href="https://github.com/HexaliteNetwork/java-edition/commits?author=SrGaabriel" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/santosbpd"><img src="https://avatars.githubusercontent.com/u/89719009?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Breno S.</b></sub></a><br /><a href="https://github.com/HexaliteNetwork/java-edition/commits?author=santosbpd" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/herocrife"><img src="https://avatars.githubusercontent.com/u/59402242?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Herocrife</b></sub></a><br /><a href="#design-Herocrife" title="Design">🎨</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->


## 💸 Supporting

### Starring the repository

If you do not want to help us with money, you can just star the repository, it still means a lot to us!

### Donations

You can support us by the GitHub sponsors program (**pending**) or on [OpenCollective][opencollective].


## 🎉 Third party

We depend on many third party libraries and applications, a complete list can be found [here][third-party].


## 📜 Licensing

To know about the license of this project, you can read the [LICENSE.md][license] file.


## ✨ Running locally

If you are interesting in contributing, please make sure to read our [contribution guide][contribution-guide] and join
our Discord community for further information and interaction with the developers and artists, and whoever knows what 
they are doing.

## UNIX-based systems

On UNIX-based systems the installation process is as follows:
1. Install the [Java Development Kit][jdk]. We recommend using the [IBM's Semeru][jdk-semeru] for the OpenJ9 virtual machine
and [Eclipse's Adoptium][jdk] for the Hotspot VM. We recommend installing both though.
2. Install the latest nightly build of [Rust][rust].
2. Install [Docker][docker] and [Docker Compose][docker-compose] for setting up the development environment in an easier way.
3. Install our command-line interface by running `./hexalite_cli.sh -s`, simple as that and it will be added to the PATH.
4. For composing the docker containers, you can run `hexalite -d`.
5. For preparing the server software and its configuration, you can run `hexalite -p`.
6. For building everything you'll need, you can run `hexalite -b`. For building and symlink the Minecraft plugins to 
their respective directories, you can run `hexalite -b name`, whereas `name` is one of the modules located at `arcade/`.
7. For starting the webserver, you can run `hexalite -w`. But it is worth mentioning that the rest webserver requires the
complete stack (PostgresQL, Redis, RabbitMQ) to be running though.
8. For running the Minecraft server, you can run `hexalite -m`. But it is worth mentioning that the rest webserver and
the complete stack (PostgresQL, Redis, RabbitMQ) are required to be running though.

## Windows 

The way this project is structured is by using mainly the symlinking technique, on Linux or any other UNIX-based system,
you can just use a bunch of scripts for the installation, but on Windows those are not available yet, so you may need to 
symlink manually or just move/copy the files.


[rust]: https://www.rust-lang.org/

[docker]: https://www.docker.com/

[docker-compose]: https://docs.docker.com/compose/

[jdk]: https://projects.eclipse.org/projects/adoptium.temurin

[jdk-semeru]: https://developer.ibm.com/languages/java/semeru-runtimes/downloads

[opencollective]: https://opencollective.com/hexalite

[third-party]: https://git.hexalite.org/java-edition-network/blob/main/third-party/NOTICE.md

[license]: https://git.hexalite.org/java-edition-network/blob/main/LICENSE.md

[purpur]: https://purpurmc.org

[velocity]: https://github.com/PaperMC/Velocity

[rabbitmq]: https://www.rabbitmq.com

[postgresql]: https://www.postgresql.org

[shaded]: https://git.hexalite.org/java-edition/blob/dev/next/third-party/NOTICE.md

[discord]: https://discord.hexalite.org
