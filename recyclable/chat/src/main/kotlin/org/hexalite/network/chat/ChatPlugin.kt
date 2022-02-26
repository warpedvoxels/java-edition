package org.hexalite.network.chat

import net.minecraft.network.protocol.game.ServerboundChatPacket
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.console
import org.hexalite.network.kraken.extension.findPlayer
import org.hexalite.network.kraken.extension.send
import org.hexalite.network.kraken.extension.unaryPlus
import org.hexalite.network.kraken.logging.info
import org.hexalite.network.kraken.pipeline.packet.packetPipelineInjectionSystem
import org.hexalite.network.kraken.pipeline.packet.transformPacketsIncoming
import org.hexalite.network.kraken.pipeline.packet.uuid

class ChatPlugin : KrakenPlugin(namespace = "chat") {
    override fun up() {
        +packetPipelineInjectionSystem()

        /**
         * This is just a placeholder code.
         * TODO: Chat system
         */
        transformPacketsIncoming<ServerboundChatPacket> { _, packet ->
            val player = uuid.findPlayer()
            val packet = ServerboundChatPacket(packet.message + " (transformado wow)")
            console.send("New message sent from ${player.name}: ${packet.message}")
            packet
        }

        log.info { +"All systems in this module have been &rainbow&enabled&reset&." }
    }
}