package org.hexalite.network.kraken.extension

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.BukkitDslMarker
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

//    __   _     __
//   / /  (_)__ / /____ ___  ___ ____
//  / /__/ (_-</ __/ -_) _ \/ -_) __/
// /____/_/___/\__/\__/_//_/\__/_/

interface BukkitEventListener : Listener {
    val plugin: KrakenPlugin
}

open class OpenBukkitEventListener(override val plugin: KrakenPlugin): BukkitEventListener

inline fun KrakenPlugin.readEvents(listener: Listener) = server.pluginManager.registerEvents(listener, this)

inline operator fun <T: BukkitEventListener> T.unaryPlus(): T = also {
    plugin.readEvents(this)
}

typealias EventCallback<T> = T.(listener: BukkitEventListener) -> Unit

@OptIn(ExperimentalContracts::class)
@BukkitDslMarker
inline fun <T: Event> KrakenPlugin.readEvents(
    type: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    listener: BukkitEventListener = OpenBukkitEventListener(this),
    crossinline callback: EventCallback<T>,
): BukkitEventListener {
    contract {
        callsInPlace(callback, InvocationKind.AT_LEAST_ONCE)
    }
    server.pluginManager.registerEvent(
        type.java,
        listener,
        priority,
        { _, event -> if (type.isInstance(event)) callback(event as T, listener) },
        this,
        ignoreIfCancelled
    )
    return listener
}

@OptIn(ExperimentalContracts::class)
@BukkitDslMarker
inline fun <reified T: Event> KrakenPlugin.readEvents(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    listener: BukkitEventListener = OpenBukkitEventListener(this),
    crossinline callback: EventCallback<T>,
): BukkitEventListener {
    contract {
        callsInPlace(callback, InvocationKind.AT_LEAST_ONCE)
    }
    return readEvents(T::class, priority, ignoreIfCancelled, listener, callback)
}

@OptIn(ExperimentalContracts::class)
@BukkitDslMarker
inline fun <T: Event> BukkitEventListener.readEvents(
    type: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: EventCallback<T>,
): BukkitEventListener {
    contract {
        callsInPlace(callback, InvocationKind.AT_LEAST_ONCE)
    }
    return plugin.readEvents(type, priority, ignoreIfCancelled, this, callback)
}

@OptIn(ExperimentalContracts::class)
@BukkitDslMarker
inline fun <reified T: Event> BukkitEventListener.readEvents(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: EventCallback<T>,
): BukkitEventListener {
    contract {
        callsInPlace(callback, InvocationKind.AT_LEAST_ONCE)
    }
    return readEvents(T::class, priority, ignoreIfCancelled, callback)
}

inline fun Listener.unregister() = HandlerList.unregisterAll(this)

inline operator fun Listener.unaryMinus() = unregister()
