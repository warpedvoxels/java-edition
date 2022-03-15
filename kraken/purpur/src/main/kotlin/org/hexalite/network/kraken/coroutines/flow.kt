package org.hexalite.network.kraken.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.*
import java.util.*
import kotlin.reflect.KClass

@OptIn(ExperimentalCoroutinesApi::class)
fun <T: Event> KrakenPlugin.createEventFlow(
    type: KClass<T>,
    `for`: UUID? = null,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = true,
    channel: Channel<T> = Channel(Channel.CONFLATED),
    listener: BukkitEventListener = OpenBukkitEventListener(this),
    onClose: (description: EventFlowDescription<T>) -> Unit = {},
): EventFlowDescription<T> {

    val flow = channel.consumeAsFlow()
        .onStart {
            listener.readEvents(type, priority, ignoreCancelled) {
                coroutineScope.launch(Dispatchers.IO) {
                    channel.send(this@readEvents)
                }
            }
        }
    if (`for` != null) {
        listener.readEvents<PlayerEvent> {
            if (this is PlayerQuitEvent || this is PlayerKickEvent) {
                if (player.uuid == `for`) {
                    channel.close()
                }
            }
        }
    }
    val description = EventFlowDescription(type, `for`, priority, ignoreCancelled, flow, channel)
    channel.invokeOnClose {
        listener.unregister()
        onClose(description)
    }
    return description
}

inline fun <reified T: Event> KrakenPlugin.createEventFlow(
    `for`: UUID? = null,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = true,
    channel: Channel<T> = Channel(Channel.CONFLATED),
    listener: BukkitEventListener = OpenBukkitEventListener(this),
    noinline onClose: (description: EventFlowDescription<T>) -> Unit = {},
): EventFlowDescription<T> = createEventFlow(T::class, `for`, priority, ignoreCancelled, channel, listener, onClose)

inline fun <T: PlayerEvent> Player.observe(
    plugin: KrakenPlugin,
    type: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = true,
    noinline onClose: (description: EventFlowDescription<T>) -> Unit = {},
): EventFlowDescription<T> = plugin.createEventFlow(type, uuid, priority, ignoreCancelled, onClose = onClose)

inline fun <reified T: PlayerEvent> Player.observe(
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
