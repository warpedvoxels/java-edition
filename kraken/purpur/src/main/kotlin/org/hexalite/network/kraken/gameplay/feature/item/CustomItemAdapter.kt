package org.hexalite.network.kraken.gameplay.feature.item

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEvent
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.BukkitEventListener

class CustomItemAdapter(override val plugin: KrakenPlugin): BukkitEventListener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun readPlayerInteract(event: PlayerInteractEvent) = with(event) {
        val main = player.inventory.itemInMainHand.custom()
        val off = player.inventory.itemInOffHand.custom()
        if (main != null || off != null) {
            isCancelled = true
            PlayerCustomItemInteractEvent(event, main, off).callEvent()
        }
    }
}