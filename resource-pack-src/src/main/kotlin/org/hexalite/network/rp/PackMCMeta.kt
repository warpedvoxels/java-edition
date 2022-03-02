package org.hexalite.network.rp

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PackMCMetaHolder(
    val pack: PackMCMeta,
)

@kotlinx.serialization.Serializable
data class PackMCMeta(
    @SerialName("pack_format")
    val format: Int = 8,
    val description: String,
)
