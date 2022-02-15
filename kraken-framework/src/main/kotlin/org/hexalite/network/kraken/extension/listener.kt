package org.hexalite.network.kraken.extension

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.hexalite.network.kraken.KrakenPlugin

//    __   _     __
//   / /  (_)__ / /____ ___  ___ ____
//  / /__/ (_-</ __/ -_) _ \/ -_) __/
// /____/_/___/\__/\__/_//_/\__/_/

open class BukkitEventListener(val plugin: KrakenPlugin) : Listener

inline fun KrakenPlugin.listen(listener: Listener) = server.pluginManager.registerEvents(listener, this)

inline operator fun BukkitEventListener.unaryPlus() =
    plugin.listen(this)

inline fun <reified T : Event> KrakenPlugin.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: (T) -> Unit
): BukkitEventListener {
    val listener = BukkitEventListener(this)
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