package org.hexalite.network.definition.protocol
@kotlinx.serialization.Serializable
data class PlayerDataRequest (
    val id: arrow.core.Either<java.util.UUID, String>,
)
@kotlinx.serialization.Serializable
data class PlayerDataReply (
    val player: org.hexalite.network.definition.entity.Player,
)
