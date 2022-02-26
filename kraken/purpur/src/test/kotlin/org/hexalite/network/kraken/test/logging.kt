package org.hexalite.network.kraken.test

import org.hexalite.network.kraken.configuration.KrakenConfig
import org.hexalite.network.kraken.logging.BasicLogger
import org.hexalite.network.kraken.logging.debug
import org.hexalite.network.kraken.logging.error
import org.hexalite.network.kraken.logging.system
import kotlin.test.Test

class LoggingTest {
    val conf = KrakenConfig().logging

    @Test
    fun `should print two simple colored messages`() = with(BasicLogger.Default) {
        system(conf) {
            "Hello"
        }
        debug(conf) {
            "World!"
        }
    }

    @Test
    fun `should print a well formatted exception`() = with(BasicLogger.Default) {
        try {
            throw IllegalArgumentException("[LoggingTest] => Should print a well formatted exception")
        } catch (exception: IllegalArgumentException) {
            error(conf, exception)
        }
    }
}