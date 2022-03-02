package org.hexalite.network.common.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hexalite.network.common.db.entity.Role

@Serializable
data class ApiRole(
    val id: String,
    @SerialName("tab_list_index")
    val tabListIndex: Int,
): ApiEntity {
    companion object
}

fun ApiRole.Companion.fromDatabaseEntity(entity: Role) = ApiRole(
    id = entity.id.value,
    tabListIndex = entity.tabListIndex
)