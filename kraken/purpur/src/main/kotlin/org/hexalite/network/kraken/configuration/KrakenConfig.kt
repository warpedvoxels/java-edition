package org.hexalite.network.kraken.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KrakenConfig(
    @SerialName("enable_debug_log_level")
    val enableDebugLogLevel: Boolean = false,
)