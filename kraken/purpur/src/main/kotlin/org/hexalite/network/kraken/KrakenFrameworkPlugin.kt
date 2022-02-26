package org.hexalite.network.kraken

import org.bukkit.plugin.java.JavaPlugin
import org.hexalite.network.kraken.command.brigadier.bukkit.BukkitBrigadierCompletion
import org.hexalite.network.kraken.configuration.KrakenConfig
import org.hexalite.network.kraken.extension.unaryPlus

inline val kraken: KrakenFrameworkPlugin
    get() = JavaPlugin.getPlugin(KrakenFrameworkPlugin::class.java)

class KrakenFrameworkPlugin : KrakenPlugin("kraken") {
    /**
     * The default [KrakenConfig] for this plugin. It can be (de)serialized using kotlinx.serialization
     * and the KAML plug-in.
     */
    val conf = KrakenConfig()

    override fun up() = +BukkitBrigadierCompletion(this)
}