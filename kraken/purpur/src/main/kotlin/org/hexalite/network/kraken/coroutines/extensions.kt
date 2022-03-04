@file:JvmName("KrakenCoroutineExtensions")

package org.hexalite.network.kraken.coroutines

import kotlinx.coroutines.*
import org.hexalite.network.kraken.KrakenPlugin

/**
 * Switch the context of the current coroutine to the given plugin's dispatcher.
 */
suspend fun <T> switch(dispatcher: BukkitDispatcher, block: suspend CoroutineScope.() -> T) = withContext(dispatcher as CoroutineDispatcher) {
    block.invoke(this)
}

/**
 * Switch the context of the current coroutine to the given plugin's dispatcher.
 * @param getter the function that returns the plugin's dispatcher
 */
suspend fun <T> KrakenPlugin.switch(getter: (KrakenPlugin) -> BukkitDispatcher, block: suspend CoroutineScope.() -> T) = withContext(getter(this)) {
    block.invoke(this)
}

/**
 * Launch a coroutine on the given plugin's dispatcher.
 * @param getter the function that returns the plugin's dispatcher
 */
fun <T> KrakenPlugin.launchCoroutine(getter: (KrakenPlugin) -> BukkitDispatcher, block: suspend CoroutineScope.() -> T): Job {
    val dispatcher = getter(this)
    return coroutineScope.launch(dispatcher) { block.invoke(this) }.also { job ->
        activeJobs.add(job)
        job.invokeOnCompletion {
            activeJobs.remove(job)
        }
    }
}