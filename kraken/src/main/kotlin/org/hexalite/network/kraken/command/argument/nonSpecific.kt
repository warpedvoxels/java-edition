package org.hexalite.network.kraken.command.argument

import com.mojang.brigadier.arguments.BoolArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ColorArgument
import org.hexalite.network.kraken.command.KrakenCommand

fun <S> KrakenCommand<S>.boolean(name: String) =
    createArgument(name, BoolArgumentType.bool(), BoolArgumentType::getBool)

fun KrakenCommand<CommandSourceStack>.color(name: String) =
    createArgument(name, ColorArgument.color(), ColorArgument::getColor)
