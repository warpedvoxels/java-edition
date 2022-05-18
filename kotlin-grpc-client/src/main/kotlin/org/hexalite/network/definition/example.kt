@file:kotlinx.serialization.UseSerializers(org.hexalite.network.common.serialization.UUIDSerializer::class)
package org.hexalite.network.definition.example
@kotlinx.serialization.Serializable
data class Example (
    val name: String,
    val id: Int,
    val email: String,
)
