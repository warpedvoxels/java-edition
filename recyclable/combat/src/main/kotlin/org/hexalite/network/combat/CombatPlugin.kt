package org.hexalite.network.combat

import com.github.ajalt.mordant.rendering.TextColors
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.logging.info

class CombatPlugin : KrakenPlugin(namespace = "combat") {
    override fun up() {
        log.info { "All systems in this module have been ${TextColors.brightGreen("enabled")}." }
    }

    override fun down() {
        log.info { "All systems in this module have been ${TextColors.brightRed("disabled")}." }
    }
}