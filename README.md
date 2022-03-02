# Hexalite Network
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-2-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

**Hexalite Network is an open-source modular network of Minecraft: Java Edition servers inspired by Mineclub, Wynncraft and Origin Realms**. This project aims to bring the best
experience possible to players without the need of mods.

## Table of contents

* [Technologies](#technologies)
* [Contributors](#contributors)
* [Supporting](#supporting)
* [Third party](#third-party)
* [Licensing](#licensing)

## Technologies

All of our Minecraft servers are built on top of the [Purpur][purpur] server software, proxied by [FlameCord][flamecord] (a fork of Waterfall focusing on security improvements). We
also use [PostgreSQL][postgresql] as our database, and use Redis for our multiserver caching and session system. We usually make a request to our rest webservers instead of
directly accessing the database in multiple servers to avoid unsynchronized or/and loss of data, and for communications we use [RabbitMQ][rabbitmq].

## Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="http://www.exst.fun"><img src="https://avatars.githubusercontent.com/u/45243386?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Pedro Henrique</b></sub></a><br /><a href="https://github.com/HexaliteNetwork/java-edition/commits?author=eexsty" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/SrGaabriel"><img src="https://avatars.githubusercontent.com/u/58668092?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Gabriel</b></sub></a><br /><a href="https://github.com/HexaliteNetwork/java-edition/commits?author=SrGaabriel" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

## Supporting

### Starring the repository

If you do not want to help us with money, you can just star the repository, it still means a lot to us!

### Donations

You can support us by the GitHub sponsors program (**pending**) or on [OpenCollective][opencollective].

## Third party

We depend on many third party libraries and applications, a complete list can be found [here][third-party].

## Licensing

To know more about the license of this project, you can read the [LICENSE.md][license] file.


[opencollective]: https://opencollective.com/hexalite

[third-party]: https://github.com/HexaliteNetwork/java-edition-network/blob/main/third-party/NOTICE.md

[license]: https://github.com/HexaliteNetwork/java-edition-network/blob/main/LICENSE.md

[purpur]: https://purpurmc.org

[flamecord]: https://github.com/2lstudios-mc/FlameCord

[rabbitmq]: https://www.rabbitmq.com

[postgresql]: https://www.postgresql.org