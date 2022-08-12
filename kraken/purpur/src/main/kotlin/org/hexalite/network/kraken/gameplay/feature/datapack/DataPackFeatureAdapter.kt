package org.hexalite.network.kraken.gameplay.feature.datapack

import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket
import org.bukkit.event.HandlerList
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.BukkitEventListener
import org.hexalite.network.kraken.logging.error
import org.hexalite.network.kraken.pipeline.packet.BukkitPacketPipelineListener
import org.hexalite.network.kraken.pipeline.packet.transformPacketsOutgoingUnit

class DataPackFeatureAdapter(override val plugin: KrakenPlugin) : BukkitEventListener {
    init {
        if (HandlerList.getRegisteredListeners(plugin).any { it.listener is BukkitPacketPipelineListener }) {
            setupCustomBiomeSendingPacketInjector()
        } else {
            plugin.log.error { "The packet pipeline injection system is not registered. This is required for custom biomes support." }
        }
    }

    private fun setupCustomBiomeSendingPacketInjector() =
        transformPacketsOutgoingUnit<ClientboundLevelChunkWithLightPacket> { ctx, packet, promise ->
            
        }
}