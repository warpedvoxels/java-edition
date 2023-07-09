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

@file:JvmName("HelpCommand")

package net.warpedvoxels.core.command

import net.warpedvoxels.command.argument.integer
import net.warpedvoxels.command.argument.optional
import net.warpedvoxels.command.enumeration
import net.warpedvoxels.command.lowercase
import net.warpedvoxels.command.paper.command
import net.warpedvoxels.command.paper.respond
import net.warpedvoxels.core.VoxelsCoreExtension

private enum class HelpSections {
    Discord, GitHub
}

internal val VoxelsCoreExtension.helpCommand
    get() = command(listOf("help", "h"), "voxels.help") {
        val page by args.integer("page").optional { 1 }
        args.enumeration(HelpSections::lowercase) { section, _ ->
            when (section) {
                HelpSections.Discord -> respond("[TODO, $page] Discord Section")
                HelpSections.GitHub -> respond("[TODO, $page] GitHub Section")
            }
        }
        runs {
            respond("[TODO, $page] No Section")
        }
    }