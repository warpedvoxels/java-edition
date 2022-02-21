package org.hexalite.network.kraken.logging

//    __                  _
//   / /  ___  ___ ____ _(_)__  ___ _
//  / /__/ _ \/ _ `/ _ `/ / _ \/ _ `/
// /____/\___/\_, /\_, /_/_//_/\_, /
//           /___//___/       /___/

// TODO
open class BasicLogger {
    open fun log(level: LoggingLevel, message: String, exception: Throwable? = null) {

    }

    inline fun debug(message: String) = log(LoggingLevel.Debug, message)
}