# Contributing

We would like to accept and hear what you have to offer and what you think of our project. We are always looking for new ideas and ways to improve the project, 
but there are just a few guidelines and our [code of conduct][coc] to follow in all of your interactions with the project.

## Issue tracking

You can find the issue tracker on [GitHub][issues]. If you've found a bug or have a feature request, please open an issue and we'll get back to you as soon as possible.
For better management of issues, we separate issues into the following labels:
* `bug`: Bug fixes and other issues that break the project
* `feature request`: New features and other improvements to the project
* `discussion`: Questions and comments about the project
* `docs`: Documentation related issues
* `help wanted`: Contributor help is appreciated for faster resolution
* `duplicate`: This issue or pull request already exists
* `on hold`: This issue or pull request is not ready to be worked on 
* `wontfix`: This issue or pull request can not be fixed
* `priority: <>`: This issue or pull request has a priority of <>
* `performance`: This issue or pull request is related to performance
* `proxy plugins`: This issue or pull request is related to our Waterfall plugins
* `purpur plugins`: This issue or pull request is related to our Purpur plugins
* `structuring`: This issue or pull request is related in how we can improve the project structure
* `rest server`: This issue or pull request is related to our Rest API 
* `security`: This issue or pull request is related to security
* `cli`: This issue or pull request is related to our command-line interface

## Guidelines

1. Discuss your ideas in the issue tracking section of the project before submitting a pull request.

1. Suggestions are welcome in the issues or in our Discord community, where you can find in the project's README.md file.

1. Specify what issue you are addressing in the pull request description if applicable.

1. We recommend taking a look at the way we structure Kotlin and Rust code and the way we use it for having a better understanding of the project.

### Kotlin

1. Follow our standard code style, which is 4 spaces for indentation, trailing commas, always use curly braces even for single line blocks, use experimental
contracts when possible, testing if applicable.

1. Avoid blocking code, use coroutines when possible using the dispatchers provided by Kraken Framework.

1. If adding or modifying custom items or blocks, please make sure to add, or edit translations and resource pack generation.

### Rust 

1. Follow our standard code style, which is 4 spaces for indentation, trailing commas, always use curly braces even for single line blocks and testing if applicable.

2. Please prefer thread-safe code when possible.

[issues]: https://git.hexalite.org/java-edition/issues
[coc]: https://git.hexalite.org/java-edition/blob/dev/next/CODE_OF_CONDUCT.md

