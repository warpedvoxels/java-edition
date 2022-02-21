package org.hexalite.network.kraken.pipeline.packet

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import org.bukkit.Server
import org.bukkit.craftbukkit.v1_18_R1.CraftServer
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.server
import org.hexalite.network.kraken.kraken

//    _  __    __  __         ___  _          ___
//   / |/ /__ / /_/ /___ __  / _ \(_)__  ___ / (_)__  ___
//  /    / -_) __/ __/ // / / ___/ / _ \/ -_) / / _ \/ -_)
// /_/|_/\__/\__/\__/\_, / /_/  /_/ .__/\__/_/_/_//_/\__/
//                  /___/        /_/

private val serverChannels: MutableList<Channel> = mutableListOf()
private var serverPipelineInboundHandler: ServerPipelineInboundHandler? = null

val Server.pipelineInboundHandler: ServerPipelineInboundHandler
    get() = serverPipelineInboundHandler
        ?: error("The server pipeline inbound handler was not initialized. Make sure you enabled the packet injection system by using the `+packetPipelineInjectionSystem()` function.")

val Server.channels: List<Channel>
    get() = serverChannels

/**
 * Set up the packet listening system internally.
 */
internal fun KrakenPlugin.setupInternalPacketListening() {
    val internal = (server as CraftServer).server.connection ?: error("This server does not has an internal connection.")
    for (connection in internal.connections) {
        val channel = connection.channel
        channel.pipeline().addFirst(ServerPipelineInboundHandler(this))
        serverChannels.add(channel)
    }
}

internal fun KrakenPlugin.disableInternalPacketListening() {
    serverChannels.forEach { channel ->
        val pipeline = channel.pipeline()
        channel.eventLoop().execute {
            runCatching {
                pipeline.remove(ServerPipelineInboundHandler(this))
            }
        }
    }
    serverChannels.clear()
}


//    _  __    __  __         _______                      __
//   / |/ /__ / /_/ /___ __  / ___/ /  ___ ____  ___  ___ / /
//  /    / -_) __/ __/ // / / /__/ _ \/ _ `/ _ \/ _ \/ -_) /
// /_/|_/\__/\__/\__/\_, /  \___/_//_/\_,_/_//_/_//_/\__/_/
//                  /___/

class ServerPipelineInboundHandler(val plugin: KrakenPlugin) : ChannelInboundHandlerAdapter() {
    private inner class ServerPipelineInjector : ChannelInitializer<Channel>() {
        override fun initChannel(channel: Channel?) {
            try {
                synchronized(server.channels) {
                    if (channel?.pipeline()?.get(PlayerPipelineDuplexChannel::class.java) == null) {
                        channel?.eventLoop()?.submit {
                            channel.pipeline().addBefore(
                                "packet_handler",
                                "kraken-${plugin.namespace}-after",
                                PlayerPipelineDuplexChannel(channel, DefaultPlayerPacketInTransform, DefaultPlayerPacketOutTransform)
                            )
                        }
                    }
                }
            } catch (exception: Exception) {
                kraken.logger.severe("An error occurred while initializing the server pipeline inbound handler; cannot inject incoming channel $channel.")
            }
        }
    }

    private inner class ServerPipelineChannelInitializer : ChannelInitializer<Channel>() {
        override fun initChannel(ch: Channel?) {
            ch?.pipeline()?.addLast(ServerPipelineInjector())
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        val channel = msg as? Channel? ?: return
        channel.pipeline().addFirst(ServerPipelineChannelInitializer())
        ctx?.fireChannelRead(msg)
    }
}