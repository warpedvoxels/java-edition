@file:JvmName("AdventureExt")
package org.hexalite.network.kraken.extension

import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * A convenient extension for parsing regular text into an Adventure Component.
 */
inline operator fun String.unaryPlus() = MiniMessage.miniMessage().deserialize(this)
