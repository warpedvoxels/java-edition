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
    @kotlinx.serialization.SerialName("last_seen")
    val lastSeen: kotlinx.datetime.Instant,
    @kotlinx.serialization.SerialName("created_at")
    val createdAt: kotlinx.datetime.Instant,
    @kotlinx.serialization.SerialName("updated_at")
    val updatedAt: kotlinx.datetime.Instant,
)
@kotlinx.serialization.Serializable
data class Role (
    val id: String,
    @kotlinx.serialization.SerialName("unicode_character")
    val unicodeCharacter: String,
    val color: String,
    @kotlinx.serialization.SerialName("tab_list_index")
    val tabListIndex: String,
)
