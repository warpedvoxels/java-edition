@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.arcade.ffa.entity
@kotlinx.serialization.Serializable
data class FfaPlayerStats (
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @kotlinx.serialization.cbor.ByteString
    val uuid: java.util.UUID,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val killstreak: Int,
    @kotlinx.serialization.SerialName("longest_killstreak")
    val longestKillstreak: Int,
)
