package org.hexalite.network.kraken.command.argument

import org.hexalite.network.kraken.command.KrakenCommand

inline fun <reified E : Enum<E>> KrakenCommand<*>.enumeration(enum: E, transform: (e: E) -> String = { it.name }) =
    enumValues<E>().map { string(transform(it)) }

fun String.toSnakeCase(): String = split("(?=[A-Z])".toRegex()).joinToString("_").lowercase()

fun Enum<*>.toSnakeCase() = name.toSnakeCase()