@file:Suppress("unused")

package org.hexalite.network.duels

import org.bukkit.event.player.PlayerJoinEvent
import org.hexalite.network.duels.blocks.PlaceholderBlock
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.blocks.customBlocks
import org.hexalite.network.kraken.blocks.item
import org.hexalite.network.kraken.extension.readEvents
import org.hexalite.network.kraken.extension.unaryPlus

/**
 * The entrypoint for the duels minigame.
 * @author @eexsty
 */
class DuelsPlugin : KrakenPlugin(namespace = "duels") {
    override fun up() {
        val adapter = +customBlocks(PlaceholderBlock)
        readEvents<PlayerJoinEvent> {
            player.inventory.addItem(PlaceholderBlock.item(adapter.ID))
        }
        println("yay")
    }

    override fun down() {
    }
}