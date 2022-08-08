package org.hexalite.network.kraken.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerEvent
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.*
import java.util.*
import kotlin.reflect.KClass

/**
 * Register a new event flow consumer.
 * @param type The type of the events to be listened to.
 * @param priority The priority of the event listening.
 * @param ignoreCancelled Whether to ignore cancelled events.
 */
fun <T : Event> KrakenPlugin.createEventFlow(
    kind: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    listener: BukkitEventListener = OpenBukkitEventListener(this),
    invokeOnCompletion: (job: Job, cause: Throwable?) -> Unit = { _, _ -> }
): Job {
    val job = SupervisorJob()
    readEvents(kind, priority, ignoreIfCancelled, listener) {
        launch(Async) {
            eventFlow.emit(this@readEvents)
        }
    }
    job.invokeOnCompletion {
        listener.unregister()
        invokeOnCompletion(job, it)
    }
    return job
}

/**
 * Register a new event flow consumer.
 * @param T The type of the events to be listened to.
 * @param priority The priority of the event listening.
 * @param ignoreCancelled Whether to ignore cancelled events.
 */
inline fun <reified T : Event> KrakenPlugin.createEventFlow(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    listener: BukkitEventListener = OpenBukkitEventListener(this),
    noinline invokeOnCompletion: (job: Job, cause: Throwable?) -> Unit
): Job = createEventFlow(T::class, priority, ignoreIfCancelled, listener, invokeOnCompletion)

inline fun <T : PlayerEvent> Player.observe(
    plugin: KrakenPlugin,
    kind: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = true,
    listener: BukkitEventListener = OpenBukkitEventListener(plugin),
    noinline invokeOnCompletion: (job: Job, cause: Throwable?) -> Unit = { _, _ -> },
    crossinline callback: suspend T.(job: Job) -> Unit,
): Job {
    val job = plugin.createEventFlow(kind, priority, ignoreCancelled, listener, invokeOnCompletion)
    val id = uniqueId
    (plugin.eventFlow.filter { kind.isInstance(it) && (this as PlayerEvent).player.uniqueId == id } as Flow<T>)
        .onEach { callback(it, job) }
    return job
}

inline fun <reified T : PlayerEvent> Player.observe(
    plugin: KrakenPlugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = true,
    noinline onClose: (description: EventFlowDescription<T>) -> Unit = {},
): EventFlowDescription<T> = observe(plugin, T::class, priority, ignoreCancelled, onClose)

data class EventFlowDescription<T: Event>(
    val type: KClass<T>,
    val `for`: UUID? = null,
    val priority: EventPriority = EventPriority.NORMAL,
    val ignoreCancelled: Boolean = true,
    val flow: Flow<T>,
    val channel: Channel<T>,
)
