package org.hexalite.network.kraken

import org.hexalite.network.kraken.bukkit.getPlugin
import org.hexalite.network.kraken.command.brigadier.bukkit.BukkitBrigadierCompletion
import org.hexalite.network.kraken.extension.unaryPlus

val kraken: KrakenFrameworkPlugin by getPlugin()

class KrakenFrameworkPlugin: KrakenPlugin(namespace = "kraken") {
    override fun up() {
        +BukkitBrigadierCompletion(this)
    }
}