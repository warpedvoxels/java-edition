package org.hexalite.network.kraken.command.argument

import com.mojang.brigadier.arguments.StringArgumentType
import org.hexalite.network.kraken.command.KrakenCommand

fun <S> KrakenCommand<S>.literal(
    name: String,
    type: StringArgumentType,
) = createArgument(name, type, StringArgumentType::getString)

inline fun <S> KrakenCommand<S>.word(name: String) = literal(name, StringArgumentType.word())

inline fun <S> KrakenCommand<S>.string(name: String) = literal(name, StringArgumentType.string())

inline fun <S> KrakenCommand<S>.greedyString(name: String) = literal(name, StringArgumentType.greedyString())
