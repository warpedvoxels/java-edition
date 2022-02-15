package org.hexalite.network.kraken.bukkit

import org.bukkit.Bukkit

//    ___       __    __    _ __
//   / _ )__ __/ /__ / /__ (_) /_
//  / _  / // /  '_//  '_// / __/
// /____/\_,_/_/\_\/_/\_\/_/\__/

inline val scheduler get() = Bukkit.getScheduler()

inline val server get() = Bukkit.getServer()

inline val console get() = server.consoleSender
