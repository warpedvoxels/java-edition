@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.protocol
@kotlinx.serialization.Serializable
data class ClanDataRequest (
    val id: org.hexalite.network.common.util.Either<Int, String>,
)
@kotlinx.serialization.Serializable
data class ClanDataPatchRequest (
    val name: String? = null,
    val tag: String? = null,
    val points: Int,
    @kotlinx.serialization.SerialName("owner_id")
    val ownerId: java.util.UUID? = null,
    @kotlinx.serialization.SerialName("updated_at")
    val updatedAt: kotlinx.datetime.Instant? = null,
)
@kotlinx.serialization.Serializable
data class ClanCreateRequest (
    val name: String,
    val tag: String,
)
@kotlinx.serialization.Serializable
data class ClanDataReply (
    val data: org.hexalite.network.definition.entity.Clan,
)
@kotlinx.serialization.Serializable
data class HelloRequest (
    val name: String,
)
@kotlinx.serialization.Serializable
data class HelloReply (
    val message: String,
)
@kotlinx.serialization.Serializable
enum class RedisKey {
    InternalIdentity,
}
@kotlinx.serialization.Serializable
enum class CommunicationsKey {
    DataQueue,
}
@kotlinx.serialization.Serializable
data class PlayerDataRequest (
    val id: org.hexalite.network.common.util.Either<java.util.UUID, String>,
)
@kotlinx.serialization.Serializable
data class PlayerDataPatchRequest (
    val hexes: Int? = null,
    @kotlinx.serialization.SerialName("last_username")
    val lastUsername: String? = null,
    val lastSeen: kotlinx.datetime.Instant? = null,
    val updatedAt: kotlinx.datetime.Instant? = null,
    val id: org.hexalite.network.common.util.Either<java.util.UUID, String>,
)
@kotlinx.serialization.Serializable
data class PlayerCreateRequest (
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @kotlinx.serialization.cbor.ByteString
    val uuid: java.util.UUID,
    val username: String,
)
@kotlinx.serialization.Serializable
data class PlayerDataReply (
    val data: org.hexalite.network.definition.entity.Player,
)
