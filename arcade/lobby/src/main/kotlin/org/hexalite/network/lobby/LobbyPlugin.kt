package org.hexalite.network.lobby

import org.bukkit.GameMode
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.unaryPlus
import org.hexalite.network.lobby.systems.DamageCancellation
import org.hexalite.network.lobby.systems.DoubleJump
import org.hexalite.network.lobby.systems.Scoreboard

class LobbyPlugin : KrakenPlugin(namespace = "lobby") {
    override fun up() {
        // <=-=-=-=> systems <=-=-=-=>
        +DamageCancellation()
        +DoubleJump()
        +Scoreboard()
        super.up()
    }

    companion object {
        val DEFAULT_GAMEMODE = GameMode.ADVENTURE
    }
}