package org.hexalite.network.kraken.collections

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.hexalite.network.kraken.extension.BukkitEventListener
import org.hexalite.network.kraken.extension.readEvents
import org.hexalite.network.kraken.extension.unregister

typealias PlayerCallback = (player: Player) -> Unit

interface OnlinePlayersCollection : MutableCollection<Player>, BukkitEventListener {
    var onQuitCallback: PlayerCallback?

    fun makeSureIfItIsListeningToActiveness() {
        if (size == 0) {
            unregister()
        } else if (size == 1) {
            readEvents<PlayerQuitEvent> {
                if (remove(player)) {
                    onQuitCallback?.invoke(player)
                }
            }
        }
    }

    fun onQuit(callback: PlayerCallback? = null) {
        onQuitCallback = callback
        makeSureIfItIsListeningToActiveness()
    }
}