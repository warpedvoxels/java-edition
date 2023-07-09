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

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import net.warpedvoxels.command.argument.integer
import net.warpedvoxels.command.velocity.command
import net.warpedvoxels.command.velocity.respond
import net.warpedvoxels.command.velocity.unaryPlus
import org.slf4j.Logger


@Plugin(id = "voxels-proxy-core")
public class VoxelsProxyCoreExtension @Inject constructor(
    public val logger: Logger,
    public val eventManager: EventManager,
    public val proxyServer: ProxyServer,
) {
    internal val extension =
        VelocityExtension("voxels", logger, eventManager, proxyServer)

    @Subscribe(order = PostOrder.FIRST)
    public fun initialize(event: ProxyInitializeEvent): Unit = with(extension) {
        +command("test") {
            val times by args.integer("times")
            executes {
                repeat(times) { index ->
                    respond("Hello! ${index + 1}")
                }
            }
        }
        init()
    }


    @Subscribe(order = PostOrder.FIRST)
    public fun shutdown(event: ProxyShutdownEvent) {
        extension.down()
    }
}
