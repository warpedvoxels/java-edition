package org.hexalite.network.duels.blocks

import org.hexalite.network.kraken.extension.ToolLevel
import org.hexalite.network.kraken.extension.ToolType
import org.hexalite.network.kraken.gameplay.features.blocks.CustomBlock
import org.hexalite.network.kraken.gameplay.features.blocks.setToolBasedHardness

object PlaceholderBlock: CustomBlock(2) {
    init {
        setToolBasedHardness(20, ToolLevel.Iron, ToolType.Pickaxe)
    }
}
