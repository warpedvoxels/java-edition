@file:Suppress("unused")

package org.hexalite.network.duels

import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.unaryPlus
import org.hexalite.network.kraken.logging.info

/**
 * The entrypoint for the duels minigame.
 * @author @eexsty
 */
class DuelsPlugin : KrakenPlugin(namespace = "duels") {
    override fun up() {
        log.info { +"All systems in this module have been &rainbow&enabled&reset&." }
    }

    override fun down() {
        log.info { +"All systems in this module have been &rainbow&disabled&reset&." }
    }
}