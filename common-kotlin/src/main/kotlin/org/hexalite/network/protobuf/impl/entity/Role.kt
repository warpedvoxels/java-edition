package org.hexalite.network.protobuf.impl.entity

import kotlinx.serialization.SerialName
import org.hexalite.network.common.math.Color

@kotlinx.serialization.Serializable
data class Role(
    val id: String,
    @SerialName("unicode_characters")
    val unicodeCharacters: String,
    @SerialName("tab_list_index")
    val tabListIndex: Int,
    val color: Color,
    val permissions: List<Permission>,
)

@kotlinx.serialization.Serializable
inline class Permission(val name: String)
