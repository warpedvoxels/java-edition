package org.hexalite.network.kraken.coroutines

import org.hexalite.network.kraken.KrakenPlugin
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

internal data class CoroutinePlugin(val plugin: KrakenPlugin): AbstractCoroutineContextElement(CoroutinePlugin) {
    companion object Key: CoroutineContext.Key<CoroutinePlugin>

    override fun toString(): String = "CoroutinePlugin(plugin=${plugin.name})"
}

operator fun CoroutineContext.plus(plugin: KrakenPlugin): CoroutineContext = this + CoroutinePlugin(plugin)
