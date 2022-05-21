package org.hexalite.network.grpc.client

import jdk.incubator.foreign.ResourceScope
import jdk.incubator.foreign.SegmentAllocator
import kotlinx.coroutines.*
import org.hexalite.network.common.settings.HexaliteSettings
import org.hexalite.network.grpc.client.services.PlayerService
import org.hexalite.network.panama.grpc.client.GrpcClient
import java.io.Closeable
import java.util.*

class HexaliteGrpcClient(private val settings: HexaliteSettings) : Closeable {
    private val job = SupervisorJob()
    val coroutineScope = CoroutineScope(job + Dispatchers.IO)

    internal val resources = ResourceScope.newImplicitScope()
    internal val allocator by lazy { SegmentAllocator.nativeAllocator(resources) }

    val services = Services()

    inner class Services {
        val player = PlayerService(this@HexaliteGrpcClient)
    }

    suspend fun initialize(ssl: Boolean): HexaliteGrpcClient {
        withContext(coroutineScope.coroutineContext) {
            val address = allocator.allocateUtf8String("${settings.grpc.root.ip}:${settings.grpc.root.port}")
            GrpcClient.init_services(address, ssl)
        }
        return this
    }

    override fun close() {
        GrpcClient.shutdown()
        coroutineScope.cancel()
    }
}

suspend fun main() {
    val settings = HexaliteSettings.read()
    val client = HexaliteGrpcClient(settings)
    client.initialize(settings.grpc.root.ssl)
    val player = client.services.player.retrieveData(UUID.randomUUID())
    println("got $player")
}
