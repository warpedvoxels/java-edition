package org.hexalite.network.kraken.gameplay.feature.item

import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockFeature

data class PlayerCustomBlockInteractEvent(
    val original: PlayerInteractEvent,
    val block: CustomBlockFeature,
): PlayerEvent(original.player), Cancellable {
    private var _isCancelled = false

    companion object {
        @get:JvmName("getHandlerList")
        @get:JvmStatic
        val Handlers = HandlerList()
    }

    override fun getHandlers(): HandlerList = Handlers

    override fun isCancelled(): Boolean = _isCancelled

    override fun setCancelled(cancel: Boolean) {
        _isCancelled = cancel
    }
}