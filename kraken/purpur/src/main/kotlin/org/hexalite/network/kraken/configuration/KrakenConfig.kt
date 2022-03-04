package org.hexalite.network.kraken.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KrakenConfig(
    val logging: KrakenLoggingConfig = KrakenLoggingConfig()
)

@Serializable
data class KrakenLoggingConfig(
    @SerialName("enable_debug_log_level")
    var enableDebugLogLevel: Boolean = true,
    @SerialName("enable_info_log_level")
    var enableInfoLogLevel: Boolean = true,
    @SerialName("enable_warning_log_level")
    val enableWarningLogLevel: Boolean = true,
    @SerialName("enable_error_log_level")
    var enableErrorLogLevel: Boolean = true,
    @SerialName("enable_critical_log_level")
    var enableCriticalLogLevel: Boolean = true,
    @SerialName("enable_system_log_level")
    var enableSystemLogLevel: Boolean = true,
)