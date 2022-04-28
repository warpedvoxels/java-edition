@file:UseSerializers(UUIDSerializer::class)

package org.hexalite.network.protobuf.impl.entity

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.UseSerializers
import org.hexalite.network.common.serialization.UUIDSerializer
import java.util.*

@kotlinx.serialization.Serializable
data class User(
    val uuid: UUID,
    val hexes: Long,
    @SerialName("last_username")
    val lastUsername: String = uuid.toString().take(16),
    @SerialName("last_seen")
    val lastSeen: Instant = Clock.System.now(),
    @SerialName("created_at")
    val createdAt: Instant = Clock.System.now(),
    @SerialName("updated_at")
    val updatedAt: Instant = Clock.System.now(),
    val roles: List<Role> = emptyList(),
)