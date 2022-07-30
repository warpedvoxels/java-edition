package org.hexalite.network.lobby.systems

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.hexalite.network.kraken.coroutines.BukkitDispatchers
import org.hexalite.network.kraken.coroutines.launchCoroutine
import org.hexalite.network.kraken.coroutines.ticks
import org.hexalite.network.kraken.extension.listener
import org.hexalite.network.kraken.extension.readEvents
import org.hexalite.network.kraken.scoreboard.scoreboard
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

    val scoreboard = plugin.scoreboard("testing") {
        title {
            + "§b§lH"
            + "§b§lHE"
            + "§b§lHEX"
            + "§b§lHEXA"
            + "§b§lHEXAL"
            + "§b§lHEXALI"
            + "§b§lHEXALIT"
            + "§b§lHEXALITE"
        }
        entry {
            + "§aG"
            + "§bGG"
            + "§cGGG"
        }
        entry { player ->
            + "Hello, ${player.name}!"
        }
    }
    plugin.launchCoroutine(BukkitDispatchers::Async) {
        scoreboard.enableConstantTicking(5.ticks)
    }
    readEvents<PlayerJoinEvent> {
        scoreboard.tickIndividually(player)
    }
}
