package org.hexalite.network.duels.blocks

import org.hexalite.network.kraken.blocks.CustomBlock
import org.hexalite.network.kraken.blocks.setToolBasedHardness
import org.hexalite.network.kraken.extension.ToolLevel
import org.hexalite.network.kraken.extension.ToolType

object PlaceholderBlock: CustomBlock(2) {
    init {
        setToolBasedHardness(20, ToolLevel.Iron, ToolType.Pickaxe)
    }
}
