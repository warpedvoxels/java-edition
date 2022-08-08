@file:JvmName("KrakenCoroutineExtensions")

package org.hexalite.network.kraken.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.BukkitDslMarker
import kotlin.coroutines.CoroutineContext

@BukkitDslMarker
fun KrakenPlugin.launch(
    context: (KrakenPlugin) -> BukkitDispatcher,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(+context, start, block)

context(KrakenPlugin)
    inline operator fun ((KrakenPlugin) -> BukkitDispatcher).unaryPlus() = invoke(this@KrakenPlugin)

context(KrakenPlugin)
    inline operator fun CoroutineContext.plus(getter: (KrakenPlugin) -> BukkitDispatcher): CoroutineContext =
    this + getter(this@KrakenPlugin)

inline val Async get() = KrakenPlugin::async

inline val Sync get() = KrakenPlugin::sync
