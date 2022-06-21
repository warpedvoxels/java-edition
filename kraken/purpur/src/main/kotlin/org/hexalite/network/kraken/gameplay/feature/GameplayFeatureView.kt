package org.hexalite.network.kraken.gameplay.feature

import org.bukkit.NamespacedKey
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockAdapter
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockFeature
import org.hexalite.network.kraken.gameplay.feature.item.CustomItemAdapter
import org.hexalite.network.kraken.gameplay.feature.item.CustomItemFeature

interface GameplayFeature

@DslMarker
annotation class GameplayFeatureDsl

@Suppress("NOTHING_TO_INLINE")
class GameplayFeatureView(
    val plugin: KrakenPlugin,
    var blocks: MutableMap<Int, CustomBlockFeature> = mutableMapOf(),
    var items: MutableMap<Int, CustomItemFeature> = mutableMapOf(),
) {
    val id = NamespacedKey(plugin, "id")

    val blockAdapter = CustomBlockAdapter(plugin, this) {
        blocks[it]
    }

    val itemAdapter = CustomItemAdapter(plugin, this) {
        items[it]
    }

    fun retrieveCustomItem(id: Int) = items[id]

    fun retrieveCustomBlock(id: Int) = blocks[id]

    operator fun GameplayFeature.unaryPlus(): GameplayFeature? = when(this) {
        is CustomItemFeature -> items.put(textureIndex, this)
        is CustomBlockFeature -> blocks.put(textureIndex, this)
        else -> throw IllegalArgumentException("Unsupported feature type")
    }

    operator fun GameplayFeature.unaryMinus() = when(this) {
        is CustomItemFeature -> items.remove(textureIndex)
        is CustomBlockFeature -> blocks.remove(textureIndex)
        else -> throw IllegalArgumentException("Unsupported feature type")
    }

    inline operator fun Collection<GameplayFeature>.unaryPlus() = forEach { +it }

    inline operator fun Collection<GameplayFeature>.unaryMinus() = forEach { -it }

    inline operator fun Array<GameplayFeature>.unaryPlus() = forEach { +it }

    inline operator fun Array<GameplayFeature>.unaryMinus() = forEach { -it }
}