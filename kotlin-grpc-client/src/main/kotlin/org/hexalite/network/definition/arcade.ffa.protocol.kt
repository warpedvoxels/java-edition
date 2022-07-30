@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.arcade.ffa.protocol
@kotlinx.serialization.Serializable
data class FfaPlayerStatsDataRequest (
    val id: org.hexalite.network.common.util.Either<java.util.UUID, String>,
)
@kotlinx.serialization.Serializable
data class FfaPlayerStatsDataPatchRequest (
    val kills: Int? = null,
    val deaths: Int? = null,
    val assists: Int? = null,
    val killstreak: Int? = null,
    @kotlinx.serialization.SerialName("longest_killstreak")
    val longestKillstreak: Int? = null,
    val id: org.hexalite.network.common.util.Either<java.util.UUID, String>,
)
@kotlinx.serialization.Serializable
data class FfaPlayerStatsDataReply (
    val data: org.hexalite.network.definition.arcade.ffa.entity.FfaPlayerStats,
)
