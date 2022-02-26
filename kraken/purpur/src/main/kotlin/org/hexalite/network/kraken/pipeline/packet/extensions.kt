package org.hexalite.network.kraken.pipeline.packet

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ServerGamePacketListener
import org.bukkit.entity.Player
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.BukkitEventListener
import org.hexalite.network.kraken.extension.findPlayer
import org.hexalite.network.kraken.extension.uuid
import java.util.*

//    ___  _          ___            ____     __               _
//   / _ \(_)__  ___ / (_)__  ___   / __/_ __/ /____ ___  ___ (_)__  ___  ___
//  / ___/ / _ \/ -_) / / _ \/ -_) / _/ \ \ / __/ -_) _ \(_-</ / _ \/ _ \(_-<
// /_/  /_/ .__/\__/_/_/_//_/\__/ /___//_\_\\__/\__/_//_/___/_/\___/_//_/___/
//       /_/

typealias PacketTransformInContext = PlayerPipelineDuplexChannel.(ctx: ChannelHandlerContext, packet: Any) -> Any?
typealias PacketTransformOutContext = PlayerPipelineDuplexChannel.(ctx: ChannelHandlerContext, packet: Any, promise: ChannelPromise) -> Any?
typealias PacketTransformInSpecificContext<T, R> = PlayerPipelineDuplexChannel.(ctx: ChannelHandlerContext, packet: T) -> R
typealias PacketTransformOutSpecificContext<T, R> = PlayerPipelineDuplexChannel.(ctx: ChannelHandlerContext, packet: T, promise: ChannelPromise) -> R

val Player.customDuplexChannelOrNull
    get() = playerCustomDuplexChannels[uuid]

val Player.customDuplexChannel
    get() = customDuplexChannelOrNull ?: error("Player is not registered with a custom duplex channel.")

@DslMarker
@Target(AnnotationTarget.FUNCTION)
annotation class PacketDslMarker

/**
 * Sets the packet in transformation for this player.
 */
@PacketDslMarker
fun Player.setPacketInTransformation(context: PacketTransformInContext) {
    customDuplexChannel.transformIn = context
}

/**
 * Sets the packet out transformation for this player.
 */
@PacketDslMarker
fun Player.setPacketOutTransformation(context: PacketTransformOutContext) {
    customDuplexChannel.transformOut = context
}

/**
 * Sets the packet in transformation globally.
 */
@PacketDslMarker
inline fun <reified T : Packet<ServerGamePacketListener>> transformPacketsIncoming(crossinline reader: PacketTransformInSpecificContext<T, Any?>) {
    val old = DefaultPlayerPacketInTransform
    setDefaultPacketInTransformation { ctx, packet ->
        if (packet is T) {
            return@setDefaultPacketInTransformation reader(ctx, packet)
        }
        old(ctx, packet)
    }
}

/**
 * Sets the packet in transformation globally.
 */
@PacketDslMarker
inline fun <reified T : Packet<ServerGamePacketListener>> transformPacketsIncomingUnit(crossinline reader: PacketTransformInSpecificContext<T, Unit>) =
    transformPacketsIncoming<T> { ctx, packet ->
        reader(ctx, packet)
        packet
    }

/**
 * Sets the packet in transformation globally.
 */
@PacketDslMarker
inline fun <reified T : Packet<ServerGamePacketListener>> transformPacketsIncomingNull(crossinline reader: PacketTransformInSpecificContext<T, Unit>) =
    transformPacketsIncoming<T> { ctx, packet ->
        reader(ctx, packet)
        null
    }

/**
 * Sets the packet out transformation globally.
 */
@PacketDslMarker
inline fun <reified T : Packet<ClientGamePacketListener>> transformPacketsOutgoing(crossinline writer: PacketTransformOutSpecificContext<T, Any?>) {
    val old = DefaultPlayerPacketOutTransform
    setDefaultPacketOutTransformation { ctx, packet, future ->
        if (packet is T) {
            return@setDefaultPacketOutTransformation writer(ctx, packet, future)
        }
        old(ctx, packet, future)
    }
}

/**
 * Sets the packet out transformation globally.
 */
@PacketDslMarker
inline fun <reified T : Packet<ClientGamePacketListener>> transformPacketsOutgoingUnit(crossinline writer: PacketTransformOutSpecificContext<T, Unit>) =
    transformPacketsOutgoing<T> { ctx, packet, future ->
        writer(ctx, packet, future)
        packet
    }

/**
 * Sets the packet in transformation for this player.
 */
@PacketDslMarker
inline fun <reified T : Packet<ServerGamePacketListener>> Player.transformPacketsIncoming(crossinline reader: PacketTransformInSpecificContext<T, Any?>) {
    val old = customDuplexChannel.transformIn
    setPacketInTransformation { ctx, packet ->
        if (packet is T) {
            val player = uuidOrNull?.findPlayer()
            return@setPacketInTransformation if (player == this@transformPacketsIncoming) {
                reader(ctx, packet)
            } else {
                packet
            }
        }
        old(ctx, packet)
    }
}

/**
 * Sets the packet in transformation for this player.
 */
@PacketDslMarker
inline fun <reified T : Packet<ServerGamePacketListener>> Player.transformPacketsIncomingUnit(crossinline reader: PacketTransformInSpecificContext<T, Unit>) =
    transformPacketsIncoming<T> { ctx, packet ->
        reader(ctx, packet)
        packet
    }

/**
 * Sets the packet out transformation for this player.
 */
@PacketDslMarker
inline fun <reified T : Packet<ClientGamePacketListener>> Player.transformPacketsOutgoing(crossinline writer: PacketTransformOutSpecificContext<T, Any?>) {
    val old = customDuplexChannel.transformOut
    setPacketOutTransformation { ctx, packet, future ->
        if (packet is T) {
            val player = uuidOrNull?.findPlayer()
            return@setPacketOutTransformation if (player == this@transformPacketsOutgoing) {
                writer(ctx, packet, future)
            } else {
                packet
            }
        }
        old(ctx, packet, future)
    }
}

/**
 * Sets the packet out transformation for this player.
 */
@PacketDslMarker
inline fun <reified T : Packet<ClientGamePacketListener>> Player.transformPacketsOutgoingUnit(crossinline writer: PacketTransformOutSpecificContext<T, Unit>) =
    transformPacketsOutgoing<T> { ctx, packet, future ->
        writer(ctx, packet, future)
        packet
    }

/**
 * Set up the entire packet pipeline injection system.
 */
fun KrakenPlugin.packetPipelineInjectionSystem(): BukkitEventListener {
    setupInternalPacketListening()
    return BukkitPacketPipelineListener(this)
}

val PlayerPipelineDuplexChannel.uuidOrNull: UUID?
    get() = playerCustomDuplexChannels.entries.find { it.value == this }?.key

val PlayerPipelineDuplexChannel.uuid: UUID
    get() = uuidOrNull ?: error("Player is not registered with a custom duplex channel.")
