@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.datatype
@kotlinx.serialization.Serializable
data class Id (
    val data: java.util.UUID,
)
