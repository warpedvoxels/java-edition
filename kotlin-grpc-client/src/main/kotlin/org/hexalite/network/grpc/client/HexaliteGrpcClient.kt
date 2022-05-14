package org.hexalite.network.grpc.client

import io.grpc.ManagedChannel
import io.grpc.netty.NettyChannelBuilder
import org.hexalite.network.common.settings.HexaliteSettings
import org.hexalite.network.grpc.client.services.PlayerService

class HexaliteGrpcClient(val channel: ManagedChannel) {
    val services = Services()

    inner class Services {
        val player = PlayerService(this@HexaliteGrpcClient)
    }

    companion object {
        fun fromSettings(settings: HexaliteSettings) = HexaliteGrpcClient(
            NettyChannelBuilder.forAddress(settings.grpc.root.host, settings.grpc.root.port.toInt())
                .usePlaintext()
                .build()
        )
    }
}