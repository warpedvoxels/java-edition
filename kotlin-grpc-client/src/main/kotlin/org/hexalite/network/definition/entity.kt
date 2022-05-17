package org.hexalite.network.definition.entity
@kotlinx.serialization.Serializable
data class Player (
    val uuid: java.util.UUID,
    val hexes: Int,
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
