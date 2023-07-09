/*
 * WarpedVoxels, a network of Minecraft: Java Edition servers
 * Copyright (C) 2023  Pedro Henrique
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.warpedvoxels.core.utility.extension

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import net.warpedvoxels.core.architecture.PurpurExtension
import net.warpedvoxels.core.architecture.UsesExtension
import net.warpedvoxels.core.architecture.dispatchers
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import kotlin.coroutines.CoroutineContext

public interface PurpurListener : Listener, UsesExtension

public typealias EventListenerCallback<T> = T.(PurpurListener) -> Unit

/**
 * Kotlin DSL for event listening on [PurpurExtension]s.
 * @param listener          The object to attribute the listening to.
 * @param priority          The listener priority in execution.
 * @param ignoreIfCancelled Whether this listener should be triggered
 *                          even if the target event is cancelled.
 * @param callback          The code to be executed when the event is
 *                          triggered.
 */
public inline fun <reified E : Event> PurpurExtension.listen(
    listener: PurpurListener,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: EventListenerCallback<E>
): Unit = server.pluginManager.registerEvent(
    E::class.java,
    listener,
    priority,
    { _, event ->
        if (E::class.java.isInstance(event)) (event as E).callback(listener)
    },
    this,
    ignoreIfCancelled
)

/**
 * Kotlin DSL for event listening on [PurpurExtension]s.
 * @param priority          The listener priority in execution.
 * @param ignoreIfCancelled Whether this listener should be triggered
 *                          even if the target event is cancelled.
 * @param callback          The code to be executed when the event is
 *                          triggered.
 */
public inline fun <reified E : Event> PurpurListener.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: EventListenerCallback<E>
): Unit = extension.listen(this, priority, ignoreIfCancelled, callback)

/**
 * Kotlin DSL for event listening on [PurpurExtension]s.
 * @param priority          The listener priority in execution.
 * @param ignoreIfCancelled Whether this listener should be triggered
 *                          even if the target event is cancelled.
 * @param callback          The code to be executed when the event is
 *                          triggered.
 */
public inline fun <reified E : Event> PurpurExtension.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: EventListenerCallback<E>
): Unit = listen(object : PurpurListener {
    override val extension: PurpurExtension = this@listen
}, priority, ignoreIfCancelled, callback)

public typealias EventPublishingPredicate<E> = E.() -> Boolean

/**
 * Consumes a hot [Flow] of events published on a [Channel].
 *
 * @param priority          The listener priority in execution.
 * @param ignoreIfCancelled Whether this listener should be triggered
 *                          even if the target event is cancelled.
 * @param context           The [CoroutineContext] to use for
 *                          dispatching coroutines.
 * @param if                The condition to have events published to
 *                          the target [channel].
 * @param listener          The object to attribute the listening to.
 * @param channel           The channel that will have events published.
 */
public inline fun <reified E : Event> PurpurExtension.eventFlow(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline `if`: EventPublishingPredicate<E> = { true },
    context: CoroutineContext = dispatchers.async,
    listener: PurpurListener = object : PurpurListener {
        override val extension: PurpurExtension = this@eventFlow
    },
    channel: Channel<E> = Channel(Channel.RENDEZVOUS)
): Flow<E> {
    val flow = channel.consumeAsFlow().onStart {
        listener.listen<E>(priority, ignoreIfCancelled) {
            if (!`if`()) {
                return@listen
            }
            launch(context) {
                channel.send(this@listen)
            }
        }
    }
    channel.invokeOnClose {
        listener.unregister()
    }
    return flow
}

/** Stops a listener from receiving new events. */
public fun Listener.unregister(): Unit =
    HandlerList.unregisterAll(this)

/**
 * Registers a listener.
 * @param listener The listener to be registered.
 */
public fun PurpurExtension.listen(listener: Listener): Unit =
    server.pluginManager.registerEvents(listener, this)

/**
 * Registers a listener.
 */
context(PurpurExtension)
public operator fun Listener.unaryPlus(): Unit = listen(this)
