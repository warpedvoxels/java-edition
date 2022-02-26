package org.hexalite.network.kraken.collections

import org.bukkit.entity.Player
import org.hexalite.network.kraken.KrakenPlugin
import java.util.*

open class OnlinePlayersList(override val plugin: KrakenPlugin, override var onQuitCallback: PlayerCallback? = null) : LinkedList<Player>(), OnlinePlayersCollection {
    override fun add(element: Player) = super.add(element).also { makeSureIfItIsListeningToActiveness() }

    override fun addAll(elements: Collection<Player>) = super.addAll(elements).also { makeSureIfItIsListeningToActiveness() }

    override fun remove(player: Player) = super.remove(player).also {
        onQuitCallback?.invoke(player)
        makeSureIfItIsListeningToActiveness()
    }

    override fun addFirst(e: Player?) = super.addFirst(e).also { makeSureIfItIsListeningToActiveness() }

    override fun addLast(e: Player?) = super.addLast(e).also { makeSureIfItIsListeningToActiveness() }

    override fun removeFirst() = super.removeFirst().also { onQuitCallback?.invoke(it); makeSureIfItIsListeningToActiveness() }

    override fun removeLast() = super.removeLast().also { onQuitCallback?.invoke(it); makeSureIfItIsListeningToActiveness() }

    override fun removeAt(index: Int) = super.removeAt(index).also { onQuitCallback?.invoke(it); makeSureIfItIsListeningToActiveness() }

    override fun removeLastOccurrence(o: Any?) = super.removeLastOccurrence(o).also {
        if (it && o != null && o is Player) {
            onQuitCallback?.invoke(o); makeSureIfItIsListeningToActiveness()
        }
    }

    override fun clear() {
        super.clear()
        makeSureIfItIsListeningToActiveness()
    }
}

fun KrakenPlugin.onlinePlayersListOf(vararg players: Player, onQuit: PlayerCallback? = null) = OnlinePlayersList(this).apply {
    addAll(players)
    this.onQuit(onQuit)
}
