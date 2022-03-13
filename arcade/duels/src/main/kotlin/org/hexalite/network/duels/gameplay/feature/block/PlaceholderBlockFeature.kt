package org.hexalite.network.duels.gameplay.feature.block

import org.hexalite.network.kraken.extension.ToolLevel
import org.hexalite.network.kraken.extension.ToolType
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockFeature
import org.hexalite.network.kraken.gameplay.feature.block.setToolBasedHardness
import org.hexalite.network.kraken.gameplay.feature.item.CustomItemFeature

object PlaceholderBlockFeature: CustomBlockFeature(1) {
    init {
        setToolBasedHardness(20, ToolLevel.Iron, ToolType.Pickaxe)
    }
}

object PlaceholderBlockItemFeature: CustomItemFeature(1) {
    init {
        name(key = "block.hexalite.placeholder")
        attack(speed = 1.0, damage = 7.0)
    }
}
