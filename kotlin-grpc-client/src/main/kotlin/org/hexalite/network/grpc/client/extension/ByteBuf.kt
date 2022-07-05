@file:JvmName("ByteBufExtension")

package org.hexalite.network.grpc.client.extension

import jdk.incubator.foreign.MemoryAddress
import jdk.incubator.foreign.ValueLayout
import org.hexalite.network.panama.grpc.client.GrpcClient

operator fun MemoryAddress.unaryPlus(): ByteArray {
    val length = GrpcClient.get_buf_len(this)
    val data = GrpcClient.get_buf_data(this)
    val bytes = ByteArray(length.toInt()) { (data[ValueLayout.JAVA_BYTE, it.toLong()].toInt() and 0xFF).toByte() }
    GrpcClient.free_buf(this)
    return bytes
}
