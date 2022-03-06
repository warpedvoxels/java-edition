package org.hexalite.network.common.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestRole(
    val id: String,
    @SerialName("tab_list_index")
    val tabListIndex: Int,
): RestEntity {
    companion object
}
