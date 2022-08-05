package org.hexalite.network.lobby.systems

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.hexalite.network.kraken.extension.readEvents
import org.hexalite.network.lobby.LobbyPlugin

@Suppress("FunctionName")
fun LobbyPlugin.DamageCancellation() = readEvents<EntityDamageEvent> {
    val player = entity as? Player? ?: return@readEvents
    isCancelled = true
}
