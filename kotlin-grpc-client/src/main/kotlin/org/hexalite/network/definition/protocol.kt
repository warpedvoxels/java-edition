@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.protocol
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
    val id: org.hexalite.network.definition.datatype.Id,
)
@kotlinx.serialization.Serializable
data class PlayerDataPatchRequest (
    val id: org.hexalite.network.definition.datatype.Id,
    val hexes: Int,
    @kotlinx.serialization.SerialName("last_username")
    val lastUsername: String,
    val lastSeen: kotlinx.datetime.Instant,
    val createdAt: kotlinx.datetime.Instant,
    val updatedAt: kotlinx.datetime.Instant,
)
@kotlinx.serialization.Serializable
data class PlayerDataReply (
    val data: org.hexalite.network.definition.entity.Player,
)
