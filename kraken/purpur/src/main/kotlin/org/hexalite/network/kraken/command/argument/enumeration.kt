package org.hexalite.network.kraken.command.argument

import org.hexalite.network.kraken.command.KrakenCommand

inline fun <reified E : Enum<E>> KrakenCommand<*>.enumeration(transform: (e: E) -> String = { it.snakecase() }) =
    enumValues<E>().map { string(transform(it)) }

fun String.snakecase(): String = split("(?=[A-Z])".toRegex()).joinToString("_").lowercase()

fun Enum<*>.snakecase() = name.snakecase()
