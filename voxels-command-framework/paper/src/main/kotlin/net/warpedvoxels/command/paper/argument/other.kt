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

package net.warpedvoxels.command.paper.argument

import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.format.TextColor
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ColorArgument
import net.warpedvoxels.command.CommandArgument
import net.warpedvoxels.command.CommandArgumentScope
import net.warpedvoxels.command.CommandDslMarker
import net.warpedvoxels.command.SuggestionsDsl
import net.warpedvoxels.command.argument.argument

@CommandDslMarker
public fun CommandArgumentScope<CommandSourceStack>.color(
    name: String,
    suggestions: SuggestionsDsl<CommandSourceStack>? = null
): CommandArgument<CommandSourceStack, TextColor, ChatFormatting> =
    argument(
        name,
        ColorArgument.color(),
        { label ->
            val minecraft = ColorArgument.getColor(this, label)
            PaperAdventure.asAdventure(minecraft)
        },
        suggestions
    )