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

package net.warpedvoxels.core.utility.collection

import net.warpedvoxels.core.architecture.PurpurExtension
import net.warpedvoxels.core.utility.extension.PurpurListener
import net.warpedvoxels.core.utility.extension.listen
import net.warpedvoxels.core.utility.extension.unregister
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent

public typealias PlayerReceiver = (Player) -> Unit

@Suppress("MemberVisibilityCanBePrivate")
public class OnlinePlayerSet(
    override val extension: PurpurExtension,
    private val onRemove: PlayerReceiver = {},
) : HashSet<Player>(), PurpurListener {
    private fun register() {
        // Only register this event once
        if (size == 1) {
            listen<PlayerQuitEvent> {
                remove(player)
            }
        } else if (size == 0) {
            unregister()
        }
    }

    override fun add(element: Player): Boolean =
        super.add(element).also {
            if (it) register()
        }

    override fun remove(element: Player): Boolean =
        super.remove(element).also {
            if (it) {
                onRemove(element)
                register()
            }
        }
}

/** Creates a [Set] that removes [Player]s on fly when they
 * quit the server.
 * @param hook Callback that gets executed on every player
 *             removal.
 */
public fun PurpurExtension.onlinePlayerSet(
    hook: PlayerReceiver = {}
): OnlinePlayerSet = OnlinePlayerSet(this, hook)
