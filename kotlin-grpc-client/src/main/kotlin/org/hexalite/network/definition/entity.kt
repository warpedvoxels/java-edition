package org.hexalite.network.definition.entity
@kotlinx.serialization.Serializable
data class Role (
    val id: String,
    val unicodeCharacter: String,
    val color: String,
    val tabListIndex: String,
)
