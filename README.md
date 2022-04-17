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

* [💻 Technologies](#-technologies)
* [✨ Contributors](#-contributors)
* [💸  Supporting](#-supporting)
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


[opencollective]: https://opencollective.com/hexalite

[third-party]: https://git.hexalite.org/java-edition-network/blob/main/third-party/NOTICE.md

[license]: https://git.hexalite.org/java-edition-network/blob/main/LICENSE.md

[purpur]: https://purpurmc.org

[velocity]: https://github.com/PaperMC/Velocity

[rabbitmq]: https://www.rabbitmq.com

[postgresql]: https://www.postgresql.org

[shaded]: https://git.hexalite.org/java-edition/blob/dev/next/third-party/NOTICE.md

[discord]: https://discord.hexalite.org