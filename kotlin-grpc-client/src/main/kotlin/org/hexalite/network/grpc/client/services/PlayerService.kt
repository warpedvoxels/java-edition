package org.hexalite.network.grpc.client.services

import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import org.hexalite.network.definition.entity.Player
import org.hexalite.network.grpc.client.HexaliteGrpcClient
import org.hexalite.network.grpc.client.extension.byteBuf
import org.hexalite.network.panama.grpc.client.GrpcClient
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
class PlayerService(val client: HexaliteGrpcClient) {
    suspend fun retrieveData(uuid: UUID): Player {
        val string = client.allocator.allocateUtf8String(uuid.toString())
        val bytes = withContext(client.coroutineScope.coroutineContext) {
             GrpcClient.retrieve_player_by_uuid(string).byteBuf(client.resources)
        }
        return Cbor.decodeFromByteArray(Player.serializer(), bytes)
    }
}