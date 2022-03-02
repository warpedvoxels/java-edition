package org.hexalite.network.kraken.coroutines

import com.google.auto.service.AutoService
import kotlinx.coroutines.*
import kotlinx.coroutines.internal.MainDispatcherFactory
import org.bukkit.Bukkit
import org.hexalite.network.kraken.bukkit.scheduler
import kotlin.coroutines.CoroutineContext

private inline fun CoroutineContext.plugin() = this[CoroutinePlugin]?.plugin ?: error("The provided coroutine context does not include a CoroutinePlugin.")

sealed interface MinecraftDispatcher

@OptIn(InternalCoroutinesApi::class)
@AutoService(MainDispatcherFactory::class)
class BukkitMainThreadDispatcherFactory: MainDispatcherFactory {
    override val loadPriority: Int = 0

    override fun createDispatcher(allFactories: List<MainDispatcherFactory>): MainCoroutineDispatcher = SyncMinecraftDispatcher
}

@OptIn(InternalCoroutinesApi::class)
abstract class AbstractSyncMinecraftDispatcher: Delay, MainCoroutineDispatcher(), MinecraftDispatcher {
    /**
     * Wait before executing an action in this dispatcher.
     * @param time the time to wait in milliseconds
     * @param continuation the continuation to resume after the delay
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val plugin = continuation.context.plugin()
        val task = scheduler.runTaskLater(plugin, Runnable {
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
            scheduler.runTask(context.plugin(), block)
        }
    }
}

object ImmediateSyncMinecraftDispatcher: AbstractSyncMinecraftDispatcher() {
    override val immediate: MainCoroutineDispatcher
        get() = this

    override fun isDispatchNeeded(context: CoroutineContext): Boolean = !Bukkit.isPrimaryThread()

    override fun toString(): String = "Dispatchers.Bukkit (imediate)"
}

object SyncMinecraftDispatcher: AbstractSyncMinecraftDispatcher() {
    override val immediate: MainCoroutineDispatcher
        get() = ImmediateSyncMinecraftDispatcher

    override fun toString(): String = "Dispatchers.Bukkit"
}

@OptIn(InternalCoroutinesApi::class)
object AsyncMinecraftDispatcher: Delay, CoroutineDispatcher(), MinecraftDispatcher {
    /**
     * Wait before executing an action in this dispatcher.
     * @param time the time to wait in milliseconds
     * @param continuation the continuation to resume after the delay
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val plugin = continuation.context.plugin()
        val task = scheduler.runTaskLaterAsynchronously(plugin, Runnable {
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
        val plugin = context.plugin()
        if (context.isActive) {
            scheduler.runTaskAsynchronously(plugin, block)
        }
    }
}

val Dispatchers.Bukkit: SyncMinecraftDispatcher
    get() = SyncMinecraftDispatcher

val Dispatchers.BukkitAsync: AsyncMinecraftDispatcher
    get() = AsyncMinecraftDispatcher
