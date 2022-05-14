package org.hexalite.network.grpc.client.services

import com.google.protobuf.ByteString
import org.hexalite.network.grpc.client.HexaliteGrpcClient
import org.hexalite.network.protobuf.datatype.uuid
import org.hexalite.network.protobuf.entity.Player
import org.hexalite.network.protobuf.protocol.PlayerGrpcKt
import org.hexalite.network.protobuf.protocol.playerDataRequest
import java.util.*

class PlayerService(val client: HexaliteGrpcClient) {
    val stub = PlayerGrpcKt.PlayerCoroutineStub(client.channel)

    suspend fun retrieveData(id: UUID): Player {
        val request = playerDataRequest {
            uuid = uuid {
                content = ByteString.copyFromUtf8(id.toString())
            }
        }
        return stub.retrieveData(request).player
    }
}