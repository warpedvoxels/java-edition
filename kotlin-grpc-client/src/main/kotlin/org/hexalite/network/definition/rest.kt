@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.rest
@kotlinx.serialization.Serializable
data class Authorization (
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @kotlinx.serialization.cbor.ByteString
    val sub: java.util.UUID,
    val exp: kotlinx.datetime.Instant,
    @kotlinx.serialization.SerialName("is_internal")
    val isInternal: Boolean,
)
