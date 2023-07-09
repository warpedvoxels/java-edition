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

package net.warpedvoxels.core.architecture

import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/** Returns a [Duration] equal to this [Int] number of ticks. */
public inline val Int.ticks: Duration
    get() = (this * 50)
        .toDuration(DurationUnit.MILLISECONDS)


/** Returns a [Duration] equal to this [Int] number of ticks. */
public inline val Long.ticks: Duration
    get() = (this * 50)
        .toDuration(DurationUnit.MILLISECONDS)

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
public sealed class BukkitDispatcher :
    UsesExtension, Delay, CoroutineDispatcher() {
    protected abstract fun runTask(callback: Runnable): BukkitTask

    protected abstract fun runTaskLater(delay: Long, callback: Runnable): BukkitTask

    final override fun scheduleResumeAfterDelay(
        timeMillis: Long,
        continuation: CancellableContinuation<Unit>
    ) {
        val task = runTaskLater(timeMillis / 50) {
            continuation.apply {
                resumeUndispatched(Unit)
            }
        }
        continuation.invokeOnCancellation {
            task.cancel()
        }
    }
}

public class SyncBukkitDispatcher(override val extension: PurpurExtension) : BukkitDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (Bukkit.isPrimaryThread()) {
            block.run()
        }
    }

    override fun runTask(callback: Runnable): BukkitTask =
        extension.server.scheduler.runTask(extension, callback)

    override fun runTaskLater(delay: Long, callback: Runnable): BukkitTask =
        extension.server.scheduler.runTaskLater(extension, callback, delay)
}

public class AsyncBukkitDispatcher(override val extension: PurpurExtension) : BukkitDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!Bukkit.isPrimaryThread()) {
            runTask(block)
        }
    }

    override fun runTask(callback: Runnable): BukkitTask =
        extension.server.scheduler.runTaskAsynchronously(extension, callback)

    override fun runTaskLater(delay: Long, callback: Runnable): BukkitTask =
        extension.server.scheduler.runTaskLaterAsynchronously(extension, callback, delay)
}

/**
 * Controls what threading context should coroutines be dispatched.
 */
@JvmInline
public value class BukkitDispatchers(
    override val extension: PurpurExtension
) : UsesExtension {
    public val async: BukkitDispatcher get() = AsyncBukkitDispatcher(extension)
    public val sync: BukkitDispatcher get() = AsyncBukkitDispatcher(extension)
}

/**
 * Controls what threading context should coroutines be dispatched.
 */
public inline val PurpurExtension.dispatchers: BukkitDispatchers
    get() = BukkitDispatchers(this)