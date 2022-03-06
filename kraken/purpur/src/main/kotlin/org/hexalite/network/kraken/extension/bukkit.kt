package org.hexalite.network.kraken.bukkit

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.hexalite.network.kraken.KrakenPlugin

//    ___       __    __    _ __
//   / _ )__ __/ /__ / /__ (_) /_
//  / _  / // /  '_//  '_// / __/
// /____/\_,_/_/\_\/_/\_\/_/\__/

inline val scheduler get() = Bukkit.getScheduler()

inline val server get() = Bukkit.getServer()

inline val console get() = server.consoleSender

@DslMarker
annotation class BukkitDslMarker

inline fun <reified T: KrakenPlugin> getPlugin(): Lazy<T> = lazy {
    JavaPlugin.getPlugin(T::class.java)
}