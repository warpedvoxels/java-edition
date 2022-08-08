package org.hexalite.network.kraken.coroutines

import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.scheduler
import kotlin.coroutines.CoroutineContext

@OptIn(InternalCoroutinesApi::class)
class BukkitDispatcher(val plugin: KrakenPlugin, val async: Boolean = true): Delay, CoroutineDispatcher() {
    /**
     * Wait before executing an action in this dispatcher.
     * @param timeMillis the time to wait in milliseconds
     * @param continuation the continuation to resume after the delay
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val task = if (async) scheduler.runTaskLaterAsynchronously(plugin, Runnable {
            continuation.apply {
                resumeUndispatched(Unit)
            }
        }, timeMillis / 50) else scheduler.runTaskLater(plugin, Runnable {
            continuation.apply {
                resumeUndispatched(Unit)
            }
        }, timeMillis / 50)
        continuation.invokeOnCancellation {
            task.cancel()
        }
    }

    /**
     * Execute an (a)synchronous action in this dispatcher.
     * @param context the coroutine context
     * @param block the action to execute
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (context.isActive) {
            if (async && !Bukkit.isPrimaryThread()) {
                scheduler.runTaskAsynchronously(plugin, block)
            } else {
                scheduler.runTask(plugin, block)
            }
        }
    }
}

