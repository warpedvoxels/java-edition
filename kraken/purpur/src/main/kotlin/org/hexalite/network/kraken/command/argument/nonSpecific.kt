package org.hexalite.network.kraken.command.argument

import com.mojang.brigadier.arguments.BoolArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ColorArgument
import org.hexalite.network.kraken.command.dsl.CommandArgumentsScope
import org.hexalite.network.kraken.command.dsl.SuggestionsDsl

fun CommandArgumentsScope.boolean(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, BoolArgumentType.bool(), BoolArgumentType::getBool, suggestions)

fun CommandArgumentsScope.color(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, ColorArgument.color(), ColorArgument::getColor, suggestions)
