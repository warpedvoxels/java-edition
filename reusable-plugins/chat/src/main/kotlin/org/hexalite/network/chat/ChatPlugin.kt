package org.hexalite.network.chat

import org.hexalite.network.kraken.KrakenPlugin

class ChatPlugin : KrakenPlugin(namespace = "chat") {
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
        super.up()
    }
}