//package org.hexalite.network.kraken.test
//
//import org.hexalite.network.kraken.configuration.KrakenConfig
//import org.hexalite.network.kraken.logging.BasicLogger
//import org.hexalite.network.kraken.logging.debug
//import org.hexalite.network.kraken.logging.error
//import org.hexalite.network.kraken.logging.system
//import kotlin.test.Test
//
//class LoggingTest {
//    val conf = KrakenConfig().logging
//    val logger = BasicLogger("Testing", conf)
//
//    @Test
//    fun `should print two simple colored messages`() = with(logger) {
//        system {
//            "Hello"
//        }
//        debug {
//            "World!"
//        }
//    }
//
//    @Test
//    fun `should print a well formatted exception`() = with(logger) {
//        try {
//            throw IllegalArgumentException("[LoggingTest] => Should print a well formatted exception")
//        } catch (exception: IllegalArgumentException) {
//            error(exception)
//        }
//    }
//}