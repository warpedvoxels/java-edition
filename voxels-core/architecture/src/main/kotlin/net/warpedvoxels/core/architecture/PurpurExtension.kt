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

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

public abstract class PurpurExtension(public val namespace: String) : JavaPlugin(), CoroutineScope {
    internal val lifecycles: MutableSet<PluginLifecycleProperty<Any>> =
        mutableSetOf()

    private val job: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext =
        job + CoroutineName(namespace)

    public open fun enable() {}

    public open fun disable() {}

    final override fun onEnable() {
        lifecycles.forEach {
            it.listeners.enable()
            it.isReady = true
        }
        enable()
    }

    final override fun onDisable() {
        lifecycles.forEach {
            it.listeners.disable(it.value)
            it.isReady = false
        }
        disable()
        job.cancel()
    }
}