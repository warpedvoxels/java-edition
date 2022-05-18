package org.hexalite.network.chat

import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextColors.brightRed
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.logging.info

class ChatPlugin: KrakenPlugin(namespace = "chat") {
    override fun up() {
//        // This is placeholder code to test the resource pack functionality.
//        val role = EliteRole
//        readEvents<AsyncChatEvent> {
//            isCancelled = true
//            server.broadcast(text {
//                content(role.unicode)
//                    .append(Component.text(" ${player.name}").color(role.color))
//                    .append(Component.text(": "))
//                    .append(message().color(role.color))
//            })
//        }

        log.info { "All systems in this module have been ${brightGreen("enabled")}." }
    }

    override fun down() {
        log.info { "All systems in this module have been ${brightRed("disabled")}." }
    }
}