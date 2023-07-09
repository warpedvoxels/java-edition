/*
 * WarpedVoxels, a network of Minecraft: Java Edition servers
 * Copyright (C) 2023  Pedro Henrique
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.warpedvoxels.command.velocity.argument

import com.mojang.brigadier.arguments.StringArgumentType
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import net.warpedvoxels.command.*
import net.warpedvoxels.command.argument.argument
import net.warpedvoxels.command.velocity.VelocityCommandFrameworkPlatform
import kotlin.jvm.optionals.getOrNull

@CommandDslMarker
public fun CommandArgumentScope<CommandSource>.player(
    name: String,
    suggestions: SuggestionsDsl<CommandSource>? = null,
): CommandArgument<CommandSource, Player, String> {
    val platform =
        tree.platform as? VelocityCommandFrameworkPlatform ?: error("Unsupported platform for player argument.")
    return argument(
        name,
        StringArgumentType.word(),
        { input ->
            val word = StringArgumentType.getString(this, input)
            platform.extension.proxyServer.getPlayer(word)
                .getOrNull() ?: noPlayersFound()
        },
        suggestions
    )
}
