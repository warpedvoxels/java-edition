@file:Suppress("unused")

package org.hexalite.network.duels

import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.console
import org.hexalite.network.kraken.extension.send
import org.hexalite.network.kraken.extension.unaryPlus

class HexaliteDuelsPlugin : KrakenPlugin(namespace = "duels") {
    override fun up() {
        console.send(+" ")
    }
}