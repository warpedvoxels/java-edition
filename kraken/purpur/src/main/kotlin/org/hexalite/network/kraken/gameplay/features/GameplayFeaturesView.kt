package org.hexalite.network.kraken.gameplay.features

import org.bukkit.NamespacedKey
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.gameplay.features.blocks.CustomBlock
import org.hexalite.network.kraken.gameplay.features.blocks.CustomBlockAdapter

@DslMarker
annotation class GameplayFeatureDsl

class GameplayFeaturesView(
    val plugin: KrakenPlugin,
    var blocks: MutableMap<Int, CustomBlock> = mutableMapOf(),
) {
    val id = NamespacedKey(plugin, "id")

    val blockAdapter = CustomBlockAdapter(plugin, this) {
        blocks[it]
    }

    operator fun CustomBlock.unaryPlus() = blocks.put(textureIndex, this)

    fun blocks(vararg values: CustomBlock) = blocks.putAll(values.associateBy { it.textureIndex })

    fun features() = blockAdapter
}