@file:JvmName("MaterialExt")
package org.hexalite.network.kraken.extension

import org.bukkit.inventory.ItemStack

enum class ToolType {
    Pickaxe,
    Axe,
    Sword,
    Shovel,
    Hoe;

    fun matches(stack: ItemStack) = when (this) {
        Pickaxe -> stack.type.name.endsWith("_PICKAXE")
        Axe -> stack.type.name.endsWith("_AXE")
        Sword -> stack.type.name.endsWith("_SWORD")
        Shovel -> stack.type.name.endsWith("_SHOVEL")
        Hoe -> stack.type.name.endsWith("_HOE")
    }
}

enum class ToolLevel {
    Netherite,
    Diamond,
    Iron,
    Stone,
    Gold,
    Wooden;

    fun matches(stack: ItemStack) = when (this) {
        Netherite -> stack.type.name.startsWith("NETHERITE_")
        Diamond -> stack.type.name.startsWith("DIAMOND_")
        Iron -> stack.type.name.startsWith("IRON_")
        Stone -> stack.type.name.startsWith("STONE_")
        Gold -> stack.type.name.startsWith("GOLDEN_")
        Wooden -> stack.type.name.startsWith("WOODEN_")
    }

    companion object {
        fun level(stack: ItemStack): ToolLevel? = values().firstOrNull { it.matches(stack) }

        val Hierarchy = linkedSetOf(Wooden, Gold, Stone, Iron, Diamond, Netherite)
    }
}
