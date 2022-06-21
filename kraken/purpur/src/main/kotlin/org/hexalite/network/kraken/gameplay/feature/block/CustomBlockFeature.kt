package org.hexalite.network.kraken.gameplay.feature.block

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.hexalite.network.kraken.extension.ToolLevel
import org.hexalite.network.kraken.extension.ToolType
import org.hexalite.network.kraken.gameplay.feature.GameplayFeature
import java.util.function.BiFunction
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.pow

typealias HardnessDecider = CustomBlockFeature.(Player) -> Int
typealias DropDecider = BlockBreakEvent.(block: CustomBlockFeature, adapter: CustomBlockAdapter) -> ItemStack?

@DslMarker
annotation class CustomBlockDslMarker

@OptIn(ExperimentalContracts::class)
open class CustomBlockFeature @JvmOverloads constructor(
    val textureIndex: Int,
    @CustomBlockDslMarker var hardness: HardnessDecider? = null,
    @CustomBlockDslMarker var onDrop: DropDecider? = { custom, adapter -> custom.stack(adapter.view) },
    var placeSound: String? = Sound.BLOCK_SAND_PLACE.key.key,
    var breakSound: String? = Sound.BLOCK_STONE_BREAK.key.key,
): GameplayFeature {
    @CustomBlockDslMarker
    @JvmName("withHardness")
    fun hardness(callback: HardnessDecider): CustomBlockFeature {
        contract {
            callsInPlace(callback, InvocationKind.AT_LEAST_ONCE)
        }
        hardness = callback
        return this
    }

    @CustomBlockDslMarker
    @JvmName("withDrop")
    fun drop( callback: DropDecider): CustomBlockFeature {
        contract {
            callsInPlace(callback, InvocationKind.AT_LEAST_ONCE)
        }
        onDrop = callback
        return this
    }

    @JvmName("withDrop")
    fun drop(function: BiFunction<CustomBlockFeature, CustomBlockAdapter, ItemStack?>) = apply {
        onDrop = { custom, adapter ->
            function.apply(custom, adapter)
        }
    }

    @JvmName("withHardness")
    fun hardness(function: BiFunction<CustomBlockFeature, Player, Int>) = apply {
        hardness = { player ->
            function.apply(this, player)
        }
    }

    @CustomBlockDslMarker
    @JvmName("withHardness")
    @JvmOverloads
    fun hardness(
        base: Int,
        minimalLevel: ToolLevel?,
        type: ToolType,
        drop: DropDecider? = { custom, adapter -> custom.stack(adapter.view) },
    ) = apply {
        val hierarchy = ToolLevel.Hierarchy
        val minimumIndex = hierarchy.indexOf(minimalLevel)
        hardness block@{ player ->
            val item = player.inventory.itemInMainHand
            val level = ToolLevel.level(item) ?: return@block base
            val levelIndex = hierarchy.indexOf(level)
            if (!type.matches(item) || levelIndex < minimumIndex) {
                return@block base
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
}

