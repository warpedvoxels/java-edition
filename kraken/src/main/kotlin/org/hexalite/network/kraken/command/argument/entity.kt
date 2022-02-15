package org.hexalite.network.kraken.command.argument

import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import org.hexalite.network.kraken.command.KrakenCommand

fun KrakenCommand<CommandSourceStack>.entity(name: String) = createArgument(name, EntityArgument.entity(), EntityArgument::getEntity)

fun KrakenCommand<CommandSourceStack>.player(name: String) = createArgument(name, EntityArgument.player(), EntityArgument::getPlayer)

fun KrakenCommand<CommandSourceStack>.entities(name: String) = createArgument(name, EntityArgument.entities(), EntityArgument::getEntities)

fun KrakenCommand<CommandSourceStack>.players(name: String) = createArgument(name, EntityArgument.players(), EntityArgument::getPlayers)
