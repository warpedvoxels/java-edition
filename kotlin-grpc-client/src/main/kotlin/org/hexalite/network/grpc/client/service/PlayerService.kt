package org.hexalite.network.grpc.client.service

import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import org.hexalite.network.definition.entity.Player
import org.hexalite.network.grpc.client.HexaliteGrpcClient
import org.hexalite.network.grpc.client.extension.asByteBuf
import org.hexalite.network.panama.grpc.client.GrpcClient
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
class PlayerService(val client: HexaliteGrpcClient) {
    suspend fun retrieveByUuid(uuid: UUID): Player {
        val string = client.allocator.allocateUtf8String(uuid.toString())
        val bytes = withContext(client.coroutineScope.coroutineContext) {
             GrpcClient.retrieve_player_by_uuid(string).asByteBuf()
        }
        return client.cbor.decodeFromByteArray(Player.serializer(), bytes)
    }
}