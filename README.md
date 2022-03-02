# Hexalite Network

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