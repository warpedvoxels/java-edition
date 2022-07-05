@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.arcade.ffa.protocol
@kotlinx.serialization.Serializable
data class FfaPlayerStatsDataRequest (
    val id: org.hexalite.network.definition.org.hexalite.network.definition.org.hexalite.network.definition.datatype.Id,
)
@kotlinx.serialization.Serializable
data class FfaPlayerStatsDataPatchRequest (
    val id: org.hexalite.network.definition.org.hexalite.network.definition.org.hexalite.network.definition.datatype.Id,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val killstreak: Int,
    val longestKillstreak: Int,
)
@kotlinx.serialization.Serializable
data class FfaPlayerStatsDataReply (
    val data: org.hexalite.network.definition.entity.FfaPlayerStats,
)
