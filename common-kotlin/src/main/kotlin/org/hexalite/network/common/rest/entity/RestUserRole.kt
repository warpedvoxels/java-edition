package org.hexalite.network.common.rest.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestUserRole(
    val id: String,
    @SerialName("tab_list_index")
    val tabListIndex: Int,
): RestEntity
