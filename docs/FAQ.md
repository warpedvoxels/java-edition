# Work-In-Progress: Frequently asked questions about Hexalite

I think this place is the best to answer some questions people do, since it is really easy to access
and Markdown is a really solid choice for this kind of issue.

## FAQ

------------------
**Q:** What is Hexalite?

**A:** Hexalite is a team of developers and artists focused on open-source Minecraft-related development. This
repository is about our work-in-progress network of servers for Minecraft: Java Edition called Hexalite Network.
------------------
**Q:** What is the technology stack used in this project?

**A:** Everything related to internal communication and programmatic database management is written in Rust and called
through FFI with the experimental project ["Panama"]. The Minecraft: Java Edition being used is Purpur, and we develop
our plugins in the Kotlin programming language. The used relational database management system is PostgreSQL, while we
use Redis for caching and RabbitMQ to communicate between modules. In overall, you can see all kind of third-party
libraries we use [here](ttps://git.hexalite.org/java-edition-network/blob/dev/next/THIRD_PARTY.md).

["Panama"]: https://openjdk.org/projects/panama
------------------
**Q:** I want to help out, is there anything I can do?

**A:** You can help translating our projects into your preferred language on our Crowdin page, which is not available
at the moment. If you are planning to contribute with development or arts, please contact us first to discuss the
changes or additions, then feel free to create a pull request!
------------------
**Q:** How can I support your work?

**A:** By starring this repository, chatting with us on our Discord community, or by donating to help paying all stuff
to make this project became alive!
