package org.hexalite.network.grpc.client.service

import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import org.hexalite.network.definition.entity.Player
import org.hexalite.network.grpc.client.HexaliteGrpcClient
import org.hexalite.network.grpc.client.extension.unaryPlus
import org.hexalite.network.panama.grpc.client.GrpcClient
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
class PlayerService(override val client: HexaliteGrpcClient): GrpcService() {
    suspend fun retrieve(uuid: UUID): Player = withContext(client.coroutineScope.coroutineContext) {
        val bytes = GrpcClient.retrieve_player_by_uuid(+uuid.toString())
        client.cbor.decodeFromByteArray(Player.serializer(), +bytes)
    }
    suspend fun retrieve(lastUsername: String): Player = withContext(client.coroutineScope.coroutineContext) {
        val bytes = GrpcClient.retrieve_player_by_last_username(+lastUsername)
        client.cbor.decodeFromByteArray(Player.serializer(), +bytes)
    }
}