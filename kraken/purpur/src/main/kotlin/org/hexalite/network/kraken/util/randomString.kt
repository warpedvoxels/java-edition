package org.hexalite.network.kraken.util

/**
 * Create a random number string with the given length.
 */
fun randomNumberString(length: IntRange): String = length.joinToString("") {
    (0..9).random().toString()
}