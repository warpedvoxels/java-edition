package org.hexalite.network.kraken.command.argument

import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import org.hexalite.network.kraken.command.dsl.CommandArgumentsScope
import org.hexalite.network.kraken.command.dsl.SuggestionsDsl

fun CommandArgumentsScope.literal(
    name: String,
    type: StringArgumentType,
    suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null
) = command.createArgument(name, type, StringArgumentType::getString, suggestions)

inline fun CommandArgumentsScope.word(
    name: String,
    noinline suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null
) = literal(name, StringArgumentType.word(), suggestions)

inline fun CommandArgumentsScope.string(
    name: String,
    noinline suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null
) = literal(name, StringArgumentType.string(), suggestions)

inline fun CommandArgumentsScope.greedyString(
    name: String,
    noinline suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null
) = literal(name, StringArgumentType.greedyString(), suggestions)
