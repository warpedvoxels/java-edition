@file:Suppress("unused")

package org.hexalite.network.duels

import com.github.ajalt.mordant.rendering.TextColors
import org.bukkit.event.player.PlayerJoinEvent
import org.hexalite.network.duels.blocks.PlaceholderBlock
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.getPlugin
import org.hexalite.network.kraken.extension.readEvents
import org.hexalite.network.kraken.extension.unaryPlus
import org.hexalite.network.kraken.gameplay.features.blocks.item
import org.hexalite.network.kraken.logging.info
import org.hexalite.network.kraken.pipeline.packet.packetPipelineInjectionSystem

/**
 * The entrypoint for the duels minigame.
 * @author @eexsty
 */
class DuelsPlugin: KrakenPlugin(namespace = "duels") {
    override fun up() {
        +packetPipelineInjectionSystem()

        // register custom blocks
        features {
            +PlaceholderBlock
        }

        // placeholder event
        readEvents<PlayerJoinEvent> {
            player.inventory.addItem(PlaceholderBlock.item(featuresView.id))
        }

        log.info { +"All systems in this module have been ${TextColors.brightGreen("enabled")}." }
    }

    override fun down() {
        log.info { +"All systems in this module have been ${TextColors.brightRed("disabled")}." }
    }
}

val duels: DuelsPlugin by getPlugin()