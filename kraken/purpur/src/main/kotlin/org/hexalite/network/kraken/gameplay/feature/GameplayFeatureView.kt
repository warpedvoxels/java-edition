package org.hexalite.network.kraken.gameplay.feature

import org.bukkit.NamespacedKey
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockAdapter
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockFeature
import org.hexalite.network.kraken.gameplay.feature.datapack.DataPackFeatureAdapter
import org.hexalite.network.kraken.gameplay.feature.datapack.biome.CustomBiomeFeature
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
    var biomes: MutableSet<CustomBiomeFeature> = mutableSetOf(),
) {
    val id = NamespacedKey(plugin, "id")

    val blockAdapter = CustomBlockAdapter(plugin)
    val itemAdapter = CustomItemAdapter(plugin)
    val dataPackAdapter = DataPackFeatureAdapter(plugin)

    operator fun GameplayFeature.unaryPlus(): GameplayFeature? = when(this) {
        is CustomItemFeature -> items.put(textureIndex, this)
        is CustomBlockFeature -> blocks.put(textureIndex, this)
        is CustomBiomeFeature -> also { register(plugin); biomes.add(this) }
        else -> throw IllegalArgumentException("Unsupported feature type")
    }

    operator fun GameplayFeature.unaryMinus() = when(this) {
        is CustomItemFeature -> items.remove(textureIndex)
        is CustomBlockFeature -> blocks.remove(textureIndex)
        is CustomBiomeFeature -> also { biomes.remove(this) }
        else -> throw IllegalArgumentException("Unsupported feature type")
    }

    inline operator fun Collection<GameplayFeature>.unaryPlus() = forEach { +it }

    inline operator fun Collection<GameplayFeature>.unaryMinus() = forEach { -it }

    inline operator fun Array<GameplayFeature>.unaryPlus() = forEach { +it }

    inline operator fun Array<GameplayFeature>.unaryMinus() = forEach { -it }
}