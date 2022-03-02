package org.hexalite.network.kraken.extension

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.BukkitDsl

//    __   _     __
//   / /  (_)__ / /____ ___  ___ ____
//  / /__/ (_-</ __/ -_) _ \/ -_) __/
// /____/_/___/\__/\__/_//_/\__/_/

interface BukkitEventListener : Listener {
    val plugin: KrakenPlugin
}

open class OpenBukkitEventListener(override val plugin: KrakenPlugin) : BukkitEventListener

inline fun KrakenPlugin.readEvents(listener: Listener) = server.pluginManager.registerEvents(listener, this)

inline operator fun <T: BukkitEventListener> T.unaryPlus(): T = also {
    plugin.readEvents(this)
}

@BukkitDsl
inline fun <reified T : Event> KrakenPlugin.readEvents(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: T.() -> Unit
): BukkitEventListener {
    val listener = OpenBukkitEventListener(this)
    server.pluginManager.registerEvent(
        T::class.java,
        listener,
        priority,
        { _, event -> if (event is T) callback(event) },
        this,
        ignoreIfCancelled
    )
    return listener
}

@BukkitDsl
inline fun <reified T : Event> BukkitEventListener.readEvents(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: T.() -> Unit
) = plugin.readEvents(priority, ignoreIfCancelled, callback)

inline fun Listener.unregister() = HandlerList.unregisterAll(this)

inline operator fun Listener.unaryMinus() = unregister()
