@file:JvmName("ItemExt")
package org.hexalite.network.kraken.extension

import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventory
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryPlayer
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.*

inline fun ItemStack.handle() = (this as CraftItemStack).handle

inline fun Inventory.handle() = (this as CraftInventory).inventory

inline fun PlayerInventory.handle() = (this as CraftInventoryPlayer).inventory
