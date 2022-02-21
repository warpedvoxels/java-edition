package org.hexalite.network.kraken.collections

import org.bukkit.entity.Player
import org.hexalite.network.kraken.KrakenPlugin

open class OnlinePlayersSet(override val plugin: KrakenPlugin, override var onQuitCallback: PlayerCallback? = null) : LinkedHashSet<Player>(), OnlinePlayersCollection {
    override fun add(element: Player) = super.add(element).also { makeSureIfItIsListeningToActiveness() }

    override fun addAll(elements: Collection<Player>) = super.addAll(elements).also { makeSureIfItIsListeningToActiveness() }

    override fun remove(player: Player) = super.remove(player).also {
        onQuitCallback?.invoke(player)
        makeSureIfItIsListeningToActiveness()
    }

    override fun clear() {
        super.clear()
        makeSureIfItIsListeningToActiveness()
    }
}

fun KrakenPlugin.onlinePlayersSetOf(vararg players: Player, onQuit: PlayerCallback? = null) = OnlinePlayersSet(this, onQuit).apply {
    addAll(players)
    this.onQuit(onQuit)
}