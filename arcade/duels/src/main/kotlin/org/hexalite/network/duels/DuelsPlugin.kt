@file:Suppress("unused")

package org.hexalite.network.duels

import org.bukkit.event.player.PlayerJoinEvent
import org.hexalite.network.duels.gameplay.feature.block.PlaceholderBlockFeature
import org.hexalite.network.duels.gameplay.feature.block.PlaceholderBlockItemFeature
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.getPlugin
import org.hexalite.network.kraken.extension.readEvents
import org.hexalite.network.kraken.extension.unaryPlus
import org.hexalite.network.kraken.pipeline.packet.packetPipelineInjectionSystem

/**
 * The entrypoint for the duels minigame.
 * @author @eexsty
 */
class DuelsPlugin: KrakenPlugin(namespace = "duels") {
    override fun up() {
        +packetPipelineInjectionSystem() // required for custom hardness

        features {
            +PlaceholderBlockFeature
            +PlaceholderBlockItemFeature
        }

        // placeholder event listener
        readEvents<PlayerJoinEvent> {
            player.inventory.addItem(PlaceholderBlockItemFeature.stack(features.id))
        }

        super.up()
    }
}

val duels: DuelsPlugin by getPlugin()