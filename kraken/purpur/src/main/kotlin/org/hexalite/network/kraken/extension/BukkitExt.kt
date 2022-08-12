@file:JvmName("BukkitExt")
package org.hexalite.network.kraken

import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.plugin.java.JavaPlugin

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

interface WithPlugin {
    val plugin: KrakenPlugin
}

inline fun Server.craftbukkit() = this as CraftServer

inline fun Server.handle() = craftbukkit().server

