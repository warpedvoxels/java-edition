package org.hexalite.network.kraken.gameplay.feature

import org.bukkit.NamespacedKey
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockAdapter
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockFeature
import org.hexalite.network.kraken.gameplay.feature.item.CustomItemAdapter
import org.hexalite.network.kraken.gameplay.feature.item.CustomItemFeature

@DslMarker
annotation class GameplayFeatureDsl

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

    operator fun CustomBlockFeature.unaryPlus() = blocks.put(textureIndex, this)

    operator fun CustomItemFeature.unaryPlus() = items.put(textureIndex, this)
}