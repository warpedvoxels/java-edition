@file:JvmName("KrakenCoroutineExtensions")

package org.hexalite.network.kraken.coroutines

import kotlinx.coroutines.*
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.scheduler
import java.lang.Runnable
import kotlin.coroutines.resume
import kotlin.time.Duration

@Suppress("FunctionName", "unused")
object MinecraftDispatchers {
    private val syncDispatchers = hashMapOf<KrakenPlugin, MinecraftDispatcher>()
    private val asyncDispatchers = hashMapOf<KrakenPlugin, MinecraftDispatcher>()

    /**
     * Creates a new sync dispatcher falling back to asynchronous code.
     * @param plugin the plugin where the scheduler code is located.
     */
    fun MainThread(plugin: KrakenPlugin) = syncDispatchers.getOrPut(plugin) { MinecraftDispatcher(plugin, false) }

    /**
     * Creates a new async dispatcher falling back to synchronous code.
     * @param plugin the plugin where the scheduler code is located.
     */
    fun AsyncThread(plugin: KrakenPlugin) = asyncDispatchers.getOrPut(plugin) { MinecraftDispatcher(plugin, true) }
}

/**
 * Delay execution of the given block for the given amount of time for a specific
 * plugin.
 * @param duration the amount of time to delay execution
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun KrakenPlugin.wait(duration: Duration, async: Boolean = true) = suspendCancellableCoroutine<Unit> {
    if (async)
        scheduler.runTaskLaterAsynchronously(this, Runnable { it.resume(Unit) }, duration.inWholeTicks)
    else
        scheduler.runTaskLater(this, Runnable { it.resume(Unit) }, duration.inWholeTicks)
}

/**
 * Switch the context of the current coroutine to the given plugin's dispatcher.
 * @param getter the function that returns the plugin's dispatcher
 */
suspend fun <T> KrakenPlugin.switch(
    getter: (KrakenPlugin) -> MinecraftDispatcher,
    block: suspend CoroutineScope.() -> T
) = withContext(getter(this)) {
    block.invoke(this)
}

/**
 * Launch a coroutine on the given plugin's dispatcher.
 * @param getter the function that returns the plugin's dispatcher
 */
fun <T> KrakenPlugin.launchIn(
    getter: (KrakenPlugin) -> MinecraftDispatcher,
    block: suspend CoroutineScope.() -> T
): Job {
    val dispatcher = getter(this)
    return coroutineScope.launch(dispatcher) { block.invoke(this) }.also { job ->
        activeJobs.add(job)
        job.invokeOnCompletion {
            activeJobs.remove(job)
        }
    }
}