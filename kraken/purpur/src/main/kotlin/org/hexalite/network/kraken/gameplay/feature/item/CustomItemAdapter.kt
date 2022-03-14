package org.hexalite.network.kraken.gameplay.feature.item

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.BukkitEventListener
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureView

class CustomItemAdapter(override val plugin: KrakenPlugin, val view: GameplayFeatureView, internal val getter: (Int) -> CustomItemFeature?): BukkitEventListener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun readPlayerInteract(event: PlayerInteractEvent) = with(event) {
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (clickedBlock?.type == Material.NOTE_BLOCK) {
            return setCancelled(true)
        }
        val main = player.inventory.itemInMainHand.asCustomOrNull(view)
        val off = player.inventory.itemInOffHand.asCustomOrNull(view)
        if (main != null || off != null) {
            PlayerCustomItemInteractEvent(event, main, off).callEvent()
        }
    }

}