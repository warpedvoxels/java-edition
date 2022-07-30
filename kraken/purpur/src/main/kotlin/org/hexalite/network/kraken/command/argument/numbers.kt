package org.hexalite.network.kraken.command.argument

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import net.minecraft.commands.CommandSourceStack
import org.hexalite.network.kraken.command.dsl.CommandArgumentsScope
import org.hexalite.network.kraken.command.dsl.SuggestionsDsl

fun CommandArgumentsScope.integer(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, IntegerArgumentType.integer(), IntegerArgumentType::getInteger, suggestions)

fun CommandArgumentsScope.long(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, LongArgumentType.longArg(), LongArgumentType::getLong, suggestions)

fun CommandArgumentsScope.double(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, DoubleArgumentType.doubleArg(), DoubleArgumentType::getDouble, suggestions)

fun CommandArgumentsScope.float(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, FloatArgumentType.floatArg(), FloatArgumentType::getFloat, suggestions)
