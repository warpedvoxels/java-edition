package org.hexalite.network.kraken.extension

import org.bukkit.Location

//    __                 __  _
//   / /  ___  _______ _/ /_(_)__  ___
//  / /__/ _ \/ __/ _ `/ __/ / _ \/ _ \
// /____/\___/\__/\_,_/\__/_/\___/_//_/

inline fun Location.component1() = x

inline fun Location.component2() = y

inline fun Location.component3() = z
