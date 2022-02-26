package org.hexalite.network.kraken.extension

import org.bukkit.Bukkit
import java.util.*

//    ___  __
//   / _ \/ /__ ___ _____ ____
//  / ___/ / _ `/ // / -_) __/
// /_/  /_/\_,_/\_, /\__/_/
//             /___/

/**
 * Find a player from the given UUID or throw an IllegalArgumentException.
 * @return The found player.
 */
inline fun UUID.findPlayer() = findPlayerOrNull() ?: throw IllegalArgumentException("Player with the UUID '$this' not found.")

/**
 * Find a player from the given UUID.
 * @return The found player or `null`.
 */
inline fun UUID.findPlayerOrNull() = Bukkit.getPlayer(this)
