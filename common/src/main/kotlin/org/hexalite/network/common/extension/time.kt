package org.hexalite.network.common.extension

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

inline fun LocalDateTime.Companion.now() = Clock.System.now().toLocalDateTime(TimeZone.UTC)