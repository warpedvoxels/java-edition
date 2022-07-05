package org.hexalite.network.grpc.client.service

import jdk.incubator.foreign.ValueLayout
import org.hexalite.network.grpc.client.HexaliteGrpcClient

sealed class GrpcService {
    abstract val client: HexaliteGrpcClient

    internal inline operator fun String.unaryPlus() = client.allocator.allocateUtf8String(this)
    
    internal inline operator fun ByteArray.unaryPlus() = client.allocator.allocateArray(ValueLayout.JAVA_BYTE, this)
}