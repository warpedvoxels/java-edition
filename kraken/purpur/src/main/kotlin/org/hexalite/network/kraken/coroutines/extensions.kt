@file:JvmName("KrakenCoroutineExtensions")

package org.hexalite.network.kraken.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hexalite.network.kraken.KrakenPlugin

/**
 * Switch the context of the current coroutine to the given plugin's dispatcher.
 */
suspend fun <T> switch(dispatcher: MinecraftDispatcher, block: suspend CoroutineScope.() -> T) = withContext(dispatcher as CoroutineDispatcher) {
    block.invoke(this)
}

/**
 * Launch a coroutine on the given plugin's dispatcher.
 */
fun <T> KrakenPlugin.launch(dispatcher: MinecraftDispatcher, block: suspend CoroutineScope.() -> T) =
    coroutineScope.launch(dispatcher as CoroutineDispatcher + this) { block.invoke(this) }.also { job ->
        activeJobs.add(job)
        job.invokeOnCompletion {
            activeJobs.remove(job)
        }
    }
