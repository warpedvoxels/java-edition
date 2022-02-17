package org.hexalite.network.kraken.extension

import de.themoep.minedown.adventure.MineDown

/**
 * A convenient extension for parsing regular text into a Minedown one with the provided [replacements].
 * @param replacements The replacements to use.
 */
inline fun String.minedown(vararg replacements: Pair<String, String>) = MineDown.parse(this, *(if (replacements.isEmpty()) emptyArray() else buildList {
    replacements.forEach {
        add(it.first)
        add(it.second)
    }
}.toTypedArray()))

/**
 * A convenient extension for parsing regular text into a Minedown one without replacements.
 */
inline operator fun String.unaryPlus() = minedown()