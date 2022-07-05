@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class, org.hexalite.network.common.serialization.ChronoInstantSerializer::class)
package org.hexalite.network.definition.example
@kotlinx.serialization.Serializable
data class Example (
    val name: String? = null,
    val id: Int,
    val email: String,
)
