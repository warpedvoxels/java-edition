@file:Suppress("unused")

package org.hexalite.network.duels

import com.github.ajalt.mordant.rendering.TextColors
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.unaryPlus
import org.hexalite.network.kraken.logging.info

/**
 * The entrypoint for the duels minigame.
 * @author @eexsty
 */
class DuelsPlugin : KrakenPlugin(namespace = "duels") {
    override fun up() {
        log.info { +"All systems in this module have been ${TextColors.brightGreen("enabled")}." }
    }

    override fun down() {
        log.info { +"All systems in this module have been ${TextColors.brightRed("disabled")}." }
    }
}