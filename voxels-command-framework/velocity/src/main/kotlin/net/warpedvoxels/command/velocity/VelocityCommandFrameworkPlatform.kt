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

package net.warpedvoxels.command.velocity

import com.velocitypowered.api.command.CommandSource
import net.warpedvoxels.command.CommandFrameworkPlatform
import net.warpedvoxels.proxy.core.VelocityExtension

@JvmInline
public value class VelocityCommandFrameworkPlatform(
    public val extension: VelocityExtension
) : CommandFrameworkPlatform<CommandSource> {
    override fun hasPermission(sender: CommandSource, permission: String): Boolean =
        sender.hasPermission(permission)
}