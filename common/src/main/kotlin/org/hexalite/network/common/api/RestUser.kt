@file:UseSerializers(UUIDSerializer::class)

package org.hexalite.network.common.api

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.hexalite.network.common.db.entity.User
import org.hexalite.network.common.db.entity.UserRole
import org.hexalite.network.common.serialization.UUIDSerializer
import java.util.*

@Serializable
data class RestUser(
    @SerialName("unique_id")
    val uniqueId: UUID,
    @SerialName("last_username")
    val lastUsername: String,
    @SerialName("last_seen")
    val lastSeen: LocalDateTime,
    val hexes: Long,
    @SerialName("created_at")
    val createdAt: LocalDateTime,
    @SerialName("updated_at")
    val updatedAt: LocalDateTime,
    val roles: List<String>,
): RestEntity {
    companion object
}

fun RestUser.Companion.fromDatabaseEntity(entity: User) = RestUser(
    uniqueId = entity.id.value,
    lastUsername = entity.lastUsername,
    lastSeen = entity.lastSeen,
    hexes = entity.hexes,
    createdAt = entity.createdAt,
    updatedAt = entity.updatedAt,
    roles = entity.roles.map(UserRole::roleId)
)