@file:JvmName("ByteBufExtension")

package org.hexalite.network.grpc.client.extension

import jdk.incubator.foreign.MemoryAddress
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope
import jdk.incubator.foreign.ValueLayout
import org.hexalite.network.panama.grpc.client.GrpcClient

fun ByteArray.toAsciiHexString() = joinToString("") {
    if (it in 32..127) it.toInt().toChar().toString() else
        "{${it.toUByte().toString(16).padStart(2, '0').uppercase()}}"
}


fun MemoryAddress.byteBuf(scope: ResourceScope): ByteArray {
    val length = GrpcClient.get_buf_len(this)
    val data = GrpcClient.get_buf_data(this)
    val segment = MemorySegment.ofAddress(data, length, scope)
    val bytes = segment.toArray(ValueLayout.JAVA_BYTE)
        .also {
            println(it.toAsciiHexString())
        }
    GrpcClient.free_buf(this)
    return bytes
}
