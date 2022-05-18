@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class)
package org.hexalite.network.definition.rest
@kotlinx.serialization.Serializable
data class Authorization (
    val sub: java.util.UUID,
    val exp: kotlinx.datetime.Instant,
    val isInternal: Boolean,
)
