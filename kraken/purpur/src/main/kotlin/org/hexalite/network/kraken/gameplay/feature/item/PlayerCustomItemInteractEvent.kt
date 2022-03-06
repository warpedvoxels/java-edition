package org.hexalite.network.kraken.gameplay.feature.item

import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent

class PlayerCustomItemInteractEvent(
    val original: PlayerInteractEvent, val mainHand: CustomItemFeature?, val offHand: CustomItemFeature?,
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