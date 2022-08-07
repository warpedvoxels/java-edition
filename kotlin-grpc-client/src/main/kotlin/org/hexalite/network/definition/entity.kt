@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.entity
@kotlinx.serialization.Serializable
data class Clan (
    val id: UInt,
    val name: String,
    val tag: String,
    val points: UInt,
    @kotlinx.serialization.SerialName("owner_id")
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @kotlinx.serialization.cbor.ByteString
    val ownerId: java.util.UUID,
    @kotlinx.serialization.SerialName("created_at")
    val createdAt: kotlinx.datetime.Instant,
    @kotlinx.serialization.SerialName("updated_at")
    val updatedAt: kotlinx.datetime.Instant,
)
@kotlinx.serialization.Serializable
data class ClanMember (
    @kotlinx.serialization.SerialName("player_id")
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @kotlinx.serialization.cbor.ByteString
    val playerId: java.util.UUID,
    @kotlinx.serialization.SerialName("clan_id")
    val clanId: UInt,
)
@kotlinx.serialization.Serializable
data class Player (
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @kotlinx.serialization.cbor.ByteString
    val uuid: java.util.UUID,
    val hexes: UInt,
    @kotlinx.serialization.SerialName("last_username")
    val lastUsername: String,
    @kotlinx.serialization.SerialName("last_seen")
    val lastSeen: kotlinx.datetime.Instant,
    @kotlinx.serialization.SerialName("created_at")
    val createdAt: kotlinx.datetime.Instant,
    @kotlinx.serialization.SerialName("updated_at")
    val updatedAt: kotlinx.datetime.Instant,
)
@kotlinx.serialization.Serializable
data class UserRole (
    @kotlinx.serialization.SerialName("player_id")
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @kotlinx.serialization.cbor.ByteString
    val playerId: java.util.UUID,
    @kotlinx.serialization.SerialName("clan_id")
    val clanId: String,
)
