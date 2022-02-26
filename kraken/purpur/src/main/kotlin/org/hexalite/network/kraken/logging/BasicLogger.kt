package org.hexalite.network.kraken.logging

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.TextColors.white
import com.github.ajalt.mordant.terminal.Terminal
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minecraft.network.chat.TextComponent
import org.hexalite.network.kraken.configuration.KrakenLoggingConfig
import org.hexalite.network.kraken.extension.callerName
import org.hexalite.network.kraken.kraken

//    __                  _
//   / /  ___  ___ ____ _(_)__  ___ _
//  / /__/ _ \/ _ `/ _ `/ / _ \/ _ `/
// /____/\___/\_, /\_, /_/_//_/\_, /
//           /___//___/       /___/

val terminal = Terminal(tabWidth = 4, ansiLevel = AnsiLevel.TRUECOLOR)
typealias LoggingMessage = () -> Any

open class BasicLogger {
    open fun log(config: KrakenLoggingConfig, level: LoggingLevel, message: LoggingMessage? = null, exception: Throwable? = null) {
        if (!when (level) {
                LoggingLevel.System -> config.enableSystemLogLevel
                LoggingLevel.Info -> config.enableInfoLogLevel
                LoggingLevel.Warning -> config.enableWarningLogLevel
                LoggingLevel.Debug -> config.enableDebugLogLevel
                LoggingLevel.Error -> config.enableErrorLogLevel
                LoggingLevel.Critical -> config.enableCriticalLogLevel
            }
        ) return

        fun String.asFormattedText() = replace("\n", "\n${" ".repeat(level.prefix.length)}  ")

        val text = buildString {
            append(level.color(level.prefix))
            if (message != null) {
                if (level != LoggingLevel.System) {
                    val caller = message.callerName().substringAfterLast('.')
                    append(" ${white("on $caller")}: ")
                } else {
                    append(": ")
                }
                val message = message()
                append(
                    when (message) {
                        is Throwable -> message.stackTraceToString()
                        is Component -> PlainTextComponentSerializer.plainText().serialize(message)
                        is TextComponent -> message.text
                        else -> terminal.render(message)
                    }.asFormattedText()
                )
            } else {
                append(": ")
            }
            if (exception != null) {
                append(exception.stackTraceToString().asFormattedText())
            }
        }
        terminal.println(text)
    }

    companion object Default : BasicLogger()
}

inline val log get() = BasicLogger.Default

inline fun BasicLogger.debug(config: KrakenLoggingConfig = kraken.conf.logging, exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(config, LoggingLevel.Debug, message, exception)

inline fun BasicLogger.system(config: KrakenLoggingConfig = kraken.conf.logging, exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(config, LoggingLevel.System, message, exception)

inline fun BasicLogger.info(config: KrakenLoggingConfig = kraken.conf.logging, exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(config, LoggingLevel.Info, message, exception)

inline fun BasicLogger.warning(config: KrakenLoggingConfig = kraken.conf.logging, exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(config, LoggingLevel.Warning, message, exception)

inline fun BasicLogger.error(config: KrakenLoggingConfig = kraken.conf.logging, exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(config, LoggingLevel.Error, message, exception)

inline fun BasicLogger.critical(config: KrakenLoggingConfig = kraken.conf.logging, exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(config, LoggingLevel.Critical, message, exception)