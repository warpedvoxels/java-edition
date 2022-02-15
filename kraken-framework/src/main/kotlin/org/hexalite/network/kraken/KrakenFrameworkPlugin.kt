package org.hexalite.network.kraken

import org.bukkit.plugin.java.JavaPlugin
import org.hexalite.network.kraken.bukkit.listen
import org.hexalite.network.kraken.command.brigadier.bukkit.BukkitBrigadierCompletion

inline val kraken: KrakenFrameworkPlugin
    get() = JavaPlugin.getPlugin(KrakenFrameworkPlugin::class.java)

class KrakenFrameworkPlugin : KrakenPlugin() {
    override fun onEnable() = listen(BukkitBrigadierCompletion())
}