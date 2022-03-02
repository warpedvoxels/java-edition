package org.hexalite.network.duels.blocks

import org.hexalite.network.kraken.blocks.CustomBlock
import org.hexalite.network.kraken.blocks.item

object PlaceholderBlock: CustomBlock(2, onDrop = { custom, adapter ->
    block.world.dropItemNaturally(block.location.add(.5, .5, .5), custom.item(adapter.ID))
})
