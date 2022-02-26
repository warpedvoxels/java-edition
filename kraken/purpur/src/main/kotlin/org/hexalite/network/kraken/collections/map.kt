package org.hexalite.network.kraken.collections

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.BukkitEventListener
import org.hexalite.network.kraken.extension.readEvents
import org.hexalite.network.kraken.extension.unregister
import org.hexalite.network.kraken.extension.uuid
import java.util.*
import java.util.concurrent.ConcurrentHashMap

typealias PlayerMapCallback<V> = (player: Player, value: V) -> Unit
typealias UUIDMapCallback<V> = (identity: UUID, value: V) -> Unit

open class OnlinePlayersConcurrentMap<V>(override val plugin: KrakenPlugin, var onQuitCallback: PlayerMapCallback<V>? = null) : ConcurrentHashMap<Player, V>(),
    BukkitEventListener {
    fun makeSureIfItIsListeningToActiveness() {
        if (size == 0) {
            unregister()
        } else if (size == 1) {
            readEvents<PlayerQuitEvent> {
                val value = remove(player)
                if (value != null) {
                    onQuitCallback?.invoke(player, value)
                }
            }
        }
    }

    fun onQuit(callback: PlayerMapCallback<V>? = null) {
        onQuitCallback = callback
        makeSureIfItIsListeningToActiveness()
    }

    override fun put(key: Player, value: V): V? = put(key, value).also {
        makeSureIfItIsListeningToActiveness()
    }

    override fun remove(key: Player): V? = remove(key).also {
        makeSureIfItIsListeningToActiveness()
    }

    override fun remove(key: Player, value: V): Boolean = remove(key, value).also {
        makeSureIfItIsListeningToActiveness()
    }

    override fun clear() {
        super.clear()
        makeSureIfItIsListeningToActiveness()
    }
}

open class OnlineUUIDsConcurrentMap<V : Any>(override val plugin: KrakenPlugin, var onQuitCallback: UUIDMapCallback<V>? = null) : ConcurrentHashMap<UUID, V>(),
    BukkitEventListener {
    fun makeSureIfItIsListeningToActiveness() {
        if (size == 0) {
            unregister()
        } else if (size == 1) {
            readEvents<PlayerQuitEvent> {
                val value = remove(player)
                if (value != null) {
                    onQuitCallback?.invoke(player.uuid, value)
                }
            }
        }
    }

    fun onQuit(callback: UUIDMapCallback<V>? = null) {
        onQuitCallback = callback
        makeSureIfItIsListeningToActiveness()
    }

    override fun put(key: UUID, value: V): V? = super.put(key, value).also {
        makeSureIfItIsListeningToActiveness()
    }

    override fun remove(key: UUID): V? = super.remove(key).also {
        makeSureIfItIsListeningToActiveness()
    }

    override fun remove(key: UUID, value: V): Boolean = super.remove(key, value).also {
        makeSureIfItIsListeningToActiveness()
    }

    inline fun put(player: Player, value: V) = put(player.uuid, value)

    inline operator fun set(player: Player, value: V) = put(player, value)

    inline fun remove(player: Player) = remove(player.uuid)

    inline fun remove(player: Player, value: V) = remove(player.uuid, value)

    override fun clear() {
        super.clear()
        makeSureIfItIsListeningToActiveness()
    }
}

fun <V : Any> KrakenPlugin.onlinePlayersMapOf(vararg values: Pair<Player, V>, onQuit: PlayerMapCallback<V>? = null) = OnlinePlayersConcurrentMap<V>(this).apply {
    values.forEach {
        put(it.first, it.second)
    }
    this.onQuit(onQuit)
}

fun <V : Any> KrakenPlugin.onlineUUIDsMapOf(vararg values: Pair<UUID, V>, onQuit: UUIDMapCallback<V>? = null) = OnlineUUIDsConcurrentMap<V>(this).apply {
    values.forEach {
        put(it.first, it.second)
    }
    this.onQuit(onQuit)
}

