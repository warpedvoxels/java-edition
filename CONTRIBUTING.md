# Contributing

We would like to accept and hear what you have to offer and what you think of our project. We are always looking for new ideas and ways to improve the project, 
but there are just a few guidelines and our [code of conduct][coc] to follow in all of your interactions with the project.

## Issue tracking

You can find the issue tracker on [GitHub][issues]. This is used to track everything that happens in the project and to help us keep
tracking of anything that is better for the project itself.

### Feature requests

If you have any ideas of features or how to improve the project, please let us know. We are always looking those and hearing it from
the community is always great. You can submit feature requests by opening an issue in the issue tracker, make sure to provide detailed
information if possible and describe any use cases if applicable.

Feature requests are tagged as `✨ feature request` and their status will be updated by GitHub Projects, labels and the comments of
the issue.

### Bugs

If you found a bug or unexpected behaviour in anything Hexalite, please let us know by submitting a bug report in the issue tracker.
Make sure to provide detailed information and describing steps to reproduce the behaviour and any error message that you get.

Those are tagged as `🐛 bug` and their status will be updated by GitHub Projects, labels and the comments of the issue.

### Wontfix

Some tracked issues will be closed and marked as `🛠️ wontfix` if we decide they're not going to be implemented or fixed, usually
due to being misaligned with the project goals or out of scope. In most cases, we will comment on the issue with more detailed
reasoning.

### Priority and state

Some issues are tagged with priority labels, which are used to decide which issue should be worked on first and how critical or
important it is. An example of priority or state labels are the following:
* `🏷️ in progress` - the issue is already being worked on
* `❗ priority: low` - the issue is low priority and will be worked on by last
* `❗ priority: medium` - this issue is neither low or high priority and will have normal priority
* `❗ priority: high` - the issue is high priority and will be worked on first

### Categories

Issues are categorized by some labels that indicate the module an feature or bug belong to. An example of category labels are 
the following:
* `💜 purpur plugins` -the performance of the project the issue is related to the proxy plugins
* `😴 rest` - the issue is related to the rest server
* `🔌 grpc` - the issue is related to the grpc server
* `🏗️ structuring` - the issue is related to the structuring of the project
* `🔒 security` - the issue is related to the security of the project
* `🚀 performance` - the issue is related to performance improvements on one of the modules above

## Guidelines and contribution workflow

### Open issues

If you are ready to contribute to our project, start by looking at issues tagged as [`🤷 good first issue`](https://git.hexalite.org/java-edition/labels/%F0%9F%A4%B7%20good%20first%20issue) and [`help wanted`](https://github.com/playhexalite/java-edition/labels/help%20wanted). Please make
sure to fork the project and working in features in a separate branch if possible and ensure that there are no build errors by running everything with your local changes. We also recommend taking a look at the way we structure Kotlin and Rust code and the way we use it for having a better understanding of the project.

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

