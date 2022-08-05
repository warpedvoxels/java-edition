package org.hexalite.network.lobby.systems

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.hexalite.network.kraken.extension.listener
import org.hexalite.network.kraken.extension.readEvents
import org.hexalite.network.lobby.LobbyPlugin

@OptIn(ObsoleteCoroutinesApi::class)
@Suppress("FunctionName")
fun LobbyPlugin.DoubleJump() = listener {
    readEvents<PlayerJumpEvent> {
        if (player.gameMode != LobbyPlugin.DEFAULT_GAMEMODE) {
            return@readEvents
        }
    }
    readEvents<PlayerToggleFlightEvent> {
        if (player.gameMode != LobbyPlugin.DEFAULT_GAMEMODE || ! player.isFlying) {
            return@readEvents
        }
        isCancelled = true
        player.allowFlight = false
        player.velocity = player.location.direction.multiply(1.6).setY(1.0)
    }

}
