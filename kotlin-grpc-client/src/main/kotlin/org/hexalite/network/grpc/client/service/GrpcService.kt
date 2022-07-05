package org.hexalite.network.grpc.client.service

import org.hexalite.network.grpc.client.HexaliteGrpcClient

sealed class GrpcService {
    abstract val client: HexaliteGrpcClient

    internal inline operator fun String.unaryPlus() = client.allocator.allocateUtf8String(this)
}