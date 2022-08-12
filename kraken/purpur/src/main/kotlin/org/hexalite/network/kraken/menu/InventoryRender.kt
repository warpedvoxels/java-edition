package org.hexalite.network.kraken.menu

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.properties.Delegates

class InventoryRender(val menu: KrakenMenu, val player: Player) {
    var title: Component by Delegates.notNull()
    var size: Int by Delegates.notNull()

    var contents: MutableList<KrakenMenuItem> by Delegates.notNull()

    var flags: MenuFlags = MenuFlags()

    fun item(item: KrakenMenuItem, builder: KrakenMenuItem.() -> Unit = {}): KrakenMenuItem {
        contents.add(item.apply(builder))
        return item
    }

    fun refreshItems(inventory: Inventory) {
        inventory.clear()
        contents.forEach {
            inventory.setItem(it.slot, it.item)
        }
    }
}