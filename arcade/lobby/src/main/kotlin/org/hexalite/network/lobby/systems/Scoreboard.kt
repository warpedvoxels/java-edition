package org.hexalite.network.lobby.systems

import org.bukkit.event.player.PlayerJoinEvent
import org.hexalite.network.kraken.coroutines.BukkitDispatchers
import org.hexalite.network.kraken.coroutines.launchCoroutine
import org.hexalite.network.kraken.coroutines.ticks
import org.hexalite.network.kraken.extension.listener
import org.hexalite.network.kraken.extension.readEvents
import org.hexalite.network.kraken.scoreboard.scoreboard
import org.hexalite.network.lobby.LobbyPlugin

fun LobbyPlugin.Scoreboard() = listener {
    val scoreboard = plugin.scoreboard("testing") {
        title(buildList<String> {
            val title = "HEXALITE"
            for (index in 1..title.length) {
                add("§9§l${title.take(index)}")
            }
        })
        entry {
            + ""
        }
        entry { player ->
            + "Account: §b${player.name}"
        }
        entry {
            + ""
        }
    }
    plugin.launchCoroutine(BukkitDispatchers::Async) {
        scoreboard.enableConstantTicking(5.ticks)
    }
    readEvents<PlayerJoinEvent> {
        scoreboard.tickIndividually(player)
    }
}