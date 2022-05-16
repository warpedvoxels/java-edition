package org.hexalite.network.definition.rest
@kotlinx.serialization.Serializable
data class Authorization (
    val sub: java.util.UUID,
    val exp: kotlinx.datetime.Instant,
    val isInternal: Boolean,
)
