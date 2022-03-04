package org.hexalite.network.kraken.blocks

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.hexalite.network.kraken.extension.ToolLevel
import org.hexalite.network.kraken.extension.ToolType
import kotlin.math.pow

typealias HardnessDecider = CustomBlock.(Player) -> Int
typealias DropDecider = BlockBreakEvent.(block: CustomBlock, adapter: CustomBlockAdapter) -> ItemStack?

@DslMarker
annotation class CustomBlockDsl

open class CustomBlock(
    val textureIndex: Int,
    @CustomBlockDsl var hardness: HardnessDecider? = null,
    @CustomBlockDsl var onDrop: DropDecider? = { custom, adapter -> custom.item(adapter.namespace) },
    var placeSound: String? = Sound.BLOCK_SAND_PLACE.key.key,
    var breakSound: String? = Sound.BLOCK_STONE_BREAK.key.key,
) {
    @CustomBlockDsl
    inline fun hardness(noinline block: HardnessDecider) {
        hardness = block
    }

    @CustomBlockDsl
    inline fun drop(noinline block: DropDecider) {
        onDrop = block
    }
}


inline fun CustomBlock.setToolBasedHardness(
    base: Int,
    minimalLevel: ToolLevel?,
    type: ToolType,
    noinline drop: DropDecider? = { custom, adapter -> custom.item(adapter.namespace) },
) {
    val hierarchy = ToolLevel.Hierarchy
    val minimumIndex = hierarchy.indexOf(minimalLevel)
    hardness { player ->
        val item = player.inventory.itemInMainHand
        val level = ToolLevel.level(item) ?: return@hardness base
        val levelIndex = hierarchy.indexOf(level)
        if (!type.matches(item) || levelIndex < minimumIndex) {
            return@hardness base
        }
        val index = hierarchy.indexOf(level) - minimumIndex.coerceAtLeast(0)
        val hardness = 0.4 * (if (index >= 1) ((0.9).pow(index)) else 1.0)
        (base * hardness).toInt()
    }
    if (drop != null) {
        drop { block, adapter ->
            val item = player.inventory.itemInMainHand
            val level = ToolLevel.level(item)
            val levelIndex = hierarchy.indexOf(level)
            if (!type.matches(item) || levelIndex < minimumIndex) {
                return@drop null
            }
            drop(block, adapter)
        }
    }
}
