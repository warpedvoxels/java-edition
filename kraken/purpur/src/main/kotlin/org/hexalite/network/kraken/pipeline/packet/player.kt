package org.hexalite.network.kraken.pipeline.packet

import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.login.ServerboundHelloPacket
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.server.PluginDisableEvent
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.collections.onlineUUIDsMapOf
import org.hexalite.network.kraken.extension.BukkitEventListener
import org.hexalite.network.kraken.extension.unregister
import org.hexalite.network.kraken.extension.uuid
import org.hexalite.network.kraken.kraken
import org.hexalite.network.kraken.logging.debug
import org.hexalite.network.kraken.logging.log

//    _  __    __  __         ___  _          ___
//   / |/ /__ / /_/ /___ __  / _ \(_)__  ___ / (_)__  ___
//  /    / -_) __/ __/ // / / ___/ / _ \/ -_) / / _ \/ -_)
// /_/|_/\__/\__/\__/\_, / /_/  /_/ .__/\__/_/_/_//_/\__/
//                  /___/        /_/

/**
 * Returns the connection for a connected player in this server.
 */
inline val Player.connection: Connection
    get() = (this as CraftPlayer).handle.connection.connection

/**
 * Returns the name of the pipeline injection for a specific [KrakenPlugin].
 */
inline val KrakenPlugin.pipelineInjectionName: String
    get() = "kraken-$namespace-pipeline-after"

/**
 * Returns the Netty channel for a connected player in this server.
 */
val Player.channel: Channel
    get() = playerCustomDuplexChannels[uuid]?.channel ?: connection.channel

/**
 * Send a packet to player's channel pipeline.
 * @param packet the packet to send
 */
inline fun Player.sendPacket(packet: Packet<*>) = channel.pipeline().writeAndFlush(packet)

/**
 * Inject a packet listener into a player's connection pipeline.
 * @param channel The channel to inject the packet listener into.
 * @param transformIn The transform to apply to incoming packets.
 * @param transformOut The transform to apply to outgoing packets.
 */
fun KrakenPlugin.injectNettyPacketListening(
    channel: Channel,
    transformIn: PacketTransformInContext = DefaultPlayerPacketInTransform,
    transformOut: PacketTransformOutContext = DefaultPlayerPacketOutTransform,
) = injectNettyPacketListening(channel, pipelineInjectionName, transformIn, transformOut)

/**
 * A map of registered custom duplex channels.
 */
internal val playerCustomDuplexChannels by lazy {
    kraken.onlineUUIDsMapOf<PlayerPipelineDuplexChannel>()
}

/**
 * The default packet "in" handling context.
 */
var DefaultPlayerPacketInTransform: PacketTransformInContext = { _, p -> p }
    private set

/**
 * The default packet "in" handling context.
 */
var DefaultPlayerPacketOutTransform: PacketTransformOutContext = { _, p, _ -> p }
    private set

/**
 * Changes the default packet "in" transformation.
 */
@PacketDslMarker
fun setDefaultPacketInTransformation(context: PacketTransformInContext) {
    DefaultPlayerPacketInTransform = context
}

/**
 * Changes the default packet "out" transformation.
 */
@PacketDslMarker
fun setDefaultPacketOutTransformation(context: PacketTransformOutContext) {
    DefaultPlayerPacketOutTransform = context
}

/**
 * Inject a packet listener into a player's connection pipeline.
 * @param namespace The namespace of the plugin.
 * @param transformIn The packet "in" transformation.
 * @param transformOut The packet "out" transformation.
 * @param channel The channel to inject the packet listener into.
 */
private fun injectNettyPacketListening(
    channel: Channel,
    namespace: String,
    transformIn: PacketTransformInContext = DefaultPlayerPacketInTransform,
    transformOut: PacketTransformOutContext = DefaultPlayerPacketOutTransform,
): PlayerPipelineDuplexChannel {
    val pipeline = channel.pipeline()
    val identifier = "kraken-$namespace-after"
    if (pipeline.context(identifier) != null) {
        pipeline.remove(identifier)
    }
    return PlayerPipelineDuplexChannel(channel, transformIn, transformOut).also { duplex ->
        pipeline.addBefore("packet_handler", identifier, duplex)
    }
}

/**
 * Inject a packet listener into a player's connection pipeline.
 * @param namespace The namespace of the plugin.
 * @param transformIn The packet "in" transformation.
 * @param transformOut The packet "out" transformation.
 */
private fun Player.injectNettyPacketListening(
    namespace: String,
    transformIn: PacketTransformInContext = DefaultPlayerPacketInTransform,
    transformOut: PacketTransformOutContext = DefaultPlayerPacketOutTransform,
) = injectNettyPacketListening(connection.channel, namespace, transformIn, transformOut)
    .also {
        playerCustomDuplexChannels[uuid] = it
    }

/**
 * Inject a packet listener into a player's connection pipeline.
 * @param plugin The plugin that the namespace will be taken.
 * @param transformIn The packet "in" transformation.
 * @param transformOut The packet "out" transformation.
 */
fun Player.injectNettyPacketListening(
    plugin: KrakenPlugin,
    transformIn: PacketTransformInContext = DefaultPlayerPacketInTransform,
    transformOut: PacketTransformOutContext = DefaultPlayerPacketOutTransform,
) = injectNettyPacketListening(plugin.pipelineInjectionName, transformIn, transformOut)

class BukkitPacketPipelineListener(override val plugin: KrakenPlugin) : BukkitEventListener {
    @EventHandler
    fun readDisable(event: PluginDisableEvent) {
        if (event.plugin.name == plugin.name) {
            plugin.disableInternalPacketListening()
            unregister()
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun readPlayerJoin(event: PlayerJoinEvent) {
        event.player.injectNettyPacketListening(plugin)
    }
}

//    _  __    __  __         _______                      __
//   / |/ /__ / /_/ /___ __  / ___/ /  ___ ____  ___  ___ / /
//  /    / -_) __/ __/ // / / /__/ _ \/ _ `/ _ \/ _ \/ -_) /
// /_/|_/\__/\__/\__/\_, /  \___/_//_/\_,_/_//_/_//_/\__/_/
//                  /___/

/**
 * The channel that would be injected into the player's connection pipeline.
 */
class PlayerPipelineDuplexChannel(val channel: Channel, var transformIn: PacketTransformInContext, var transformOut: PacketTransformOutContext) : ChannelDuplexHandler() {
    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg != null && ctx != null) {
            if (msg is ServerboundHelloPacket) {
                log.debug { "Received a ServerboundHelloPacket from ${msg.gameProfile.id}. Setting up packet handling system for this channel..." }
                playerCustomDuplexChannels[msg.gameProfile.id] = this
            }
            val msg = transformIn(ctx, msg)
            if (msg != null) {
                super.channelRead(ctx, msg)
            }
        }
    }

    override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
        if (msg != null && ctx != null && promise != null) {
            val msg = transformOut(ctx, msg, promise)
            if (msg != null) {
                super.write(ctx, msg, promise)
            }
        }
    }
}
