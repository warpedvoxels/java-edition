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

package net.warpedvoxels.proxy.core

import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import net.warpedvoxels.proxy.core.coroutines.registerCoroutineContinuationAdapter
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext

public class VelocityExtension(
    public val namespace: String,
    public val logger: Logger,
    public val eventManager: EventManager,
    public val proxyServer: ProxyServer,
) : CoroutineScope {

    private val job: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext by lazy {
        job + CoroutineName(namespace)
    }

    init {
        registerCoroutineContinuationAdapter()
    }

    internal val lifecycles: MutableSet<PluginLifecycleProperty<Any>> =
        mutableSetOf()

    public fun init() {
        lifecycles.forEach {
            it.listeners.proxyInitialize()
            it.isReady = true
        }
    }

    public fun down() {
        lifecycles.forEach {
            it.listeners.proxyShutdown(it.value)
            it.isReady = false
        }
        job.cancel()
    }
}