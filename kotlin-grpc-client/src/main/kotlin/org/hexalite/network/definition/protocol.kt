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
    val id: java.util.UUID,
)
@kotlinx.serialization.Serializable
data class PlayerDataReply (
    val player: org.hexalite.network.definition.entity.Player,
)
