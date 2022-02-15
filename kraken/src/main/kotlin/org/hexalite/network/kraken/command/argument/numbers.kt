package org.hexalite.network.kraken.command.argument

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import org.hexalite.network.kraken.command.KrakenCommand

fun <S> KrakenCommand<S>.integer(name: String) = createArgument(name, IntegerArgumentType.integer(), IntegerArgumentType::getInteger)

fun <S> KrakenCommand<S>.long(name: String) = createArgument(name, LongArgumentType.longArg(), LongArgumentType::getLong)

fun <S> KrakenCommand<S>.double(name: String) = createArgument(name, DoubleArgumentType.doubleArg(), DoubleArgumentType::getDouble)

fun <S> KrakenCommand<S>.float(name: String) = createArgument(name, FloatArgumentType.floatArg(), FloatArgumentType::getFloat)
