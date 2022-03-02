package org.hexalite.network.kraken.blocks

import org.hexalite.network.kraken.KrakenPlugin

inline fun KrakenPlugin.customBlocks(vararg blocks: CustomBlock): CustomBlockAdapter {
    val blocks = blocks.map { it.textureIndex to it }.toMap()
    return CustomBlockAdapter({ blocks[it] }, this)
}