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

package net.warpedvoxels.core

import net.warpedvoxels.command.paper.commandSuggestions
import net.warpedvoxels.command.paper.unaryPlus
import net.warpedvoxels.core.architecture.PurpurExtension
import net.warpedvoxels.core.command.helpCommand
import net.warpedvoxels.core.utility.extension.unaryPlus

public class VoxelsCoreExtension : PurpurExtension("voxels") {
    override fun enable() {
        +commandSuggestions()
        +helpCommand
    }
}