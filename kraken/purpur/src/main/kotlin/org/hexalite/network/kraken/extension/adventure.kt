package org.hexalite.network.kraken.extension

import net.kyori.adventure.text.format.TextColor

inline fun String.color() = TextColor.fromHexString(if (startsWith('#')) this else "#$this")!!