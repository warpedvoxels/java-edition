package org.hexalite.network.kraken.command.argument

import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import org.hexalite.network.kraken.command.dsl.CommandArgumentsScope
import org.hexalite.network.kraken.command.dsl.SuggestionsDsl

fun CommandArgumentsScope.entity(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, EntityArgument.entity(), EntityArgument::getEntity, suggestions)

fun CommandArgumentsScope.player(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, EntityArgument.player(), EntityArgument::getPlayer, suggestions)

fun CommandArgumentsScope.entities(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, EntityArgument.entities(), EntityArgument::getEntities, suggestions)

fun CommandArgumentsScope.players(name: String, suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null) =
    command.createArgument(name, EntityArgument.players(), EntityArgument::getPlayers, suggestions)
