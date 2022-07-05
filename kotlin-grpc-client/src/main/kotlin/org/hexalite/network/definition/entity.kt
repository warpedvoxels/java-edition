@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.entity
@kotlinx.serialization.Serializable
data class Player (
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @kotlinx.serialization.cbor.ByteString
    val uuid: java.util.UUID,
    val hexes: Int,
    @kotlinx.serialization.SerialName("last_username")
    val lastUsername: String,
    val lastSeen: kotlinx.datetime.Instant,
    val createdAt: kotlinx.datetime.Instant,
    val updatedAt: kotlinx.datetime.Instant,
)
@kotlinx.serialization.Serializable
data class Role (
    val id: String,
    val unicodeCharacter: String,
    val color: String,
    val tabListIndex: String,
)
