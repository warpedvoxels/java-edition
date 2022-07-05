@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.arcade.ffa.protocol
@kotlinx.serialization.Serializable
data class FfaPlayerStatsDataRequest (
    val id: org.hexalite.network.definition.org.hexalite.network.definition.org.hexalite.network.definition.datatype.Id,
)
@kotlinx.serialization.Serializable
data class FfaPlayerStatsDataPatchRequest (
    val id: org.hexalite.network.definition.org.hexalite.network.definition.org.hexalite.network.definition.datatype.Id,
    val kills: Int? = null,
    val deaths: Int? = null,
    val assists: Int? = null,
    val killstreak: Int? = null,
    val longestKillstreak: Int? = null,
)
@kotlinx.serialization.Serializable
data class FfaPlayerStatsDataReply (
    val data: org.hexalite.network.definition.entity.FfaPlayerStats,
)
