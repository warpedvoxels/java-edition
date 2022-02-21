package org.hexalite.network.kraken.logging

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.*

//    __                  _
//   / /  ___  ___ ____ _(_)__  ___ _
//  / /__/ _ \/ _ `/ _ `/ / _ \/ _ `/
// /____/\___/\_, /\_, /_/_//_/\_, /
//           /___//___/       /___/

enum class LoggingLevel(val prefix: String, val color: TextColors) {
    Debug("\uD83E\uDDEA debug", brightBlue),
    Info("\uD83D\uDCE8 info", brightYellow),
    Warning("⚠️ warning", yellow),
    Error("❌ error", red),
    Severe("💀 severe", brightRed)
}