@file:JvmName("CustomBlockExtensions")

package org.hexalite.network.kraken.gameplay.feature.block

import org.bukkit.Bukkit
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.Note
import org.bukkit.block.Block
import org.bukkit.block.data.type.NoteBlock
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.WithPlugin

fun Block.textureIndex(): Int? = (this as? NoteBlock)?.run {
    (instrument.type * 25) + note.id + (if (isPowered) 400 else 0) - 26
}

inline fun Block.custom(plugin: KrakenPlugin) = plugin.features.blocks[textureIndex()]

inline fun CustomBlockFeature.stack(plugin: KrakenPlugin, amount: Int = 1, showTextureIndexInTheLore: Boolean = true) =
    plugin.features.items[textureIndex]?.stack(plugin.features.id, amount, showTextureIndexInTheLore)

context(WithPlugin)
    inline fun CustomBlockFeature.stack(amount: Int = 1, showTextureIndexInTheLore: Boolean = true) =
    stack(plugin, amount, showTextureIndexInTheLore)

context(WithPlugin)
    inline fun Block.custom() = custom(plugin)

inline fun CustomBlockFeature.applyMetadataTo(block: Block) {
    block.setType(Material.NOTE_BLOCK, false)
    block.blockData = (Bukkit.createBlockData(Material.NOTE_BLOCK) as NoteBlock).apply {
        val textureIndex = textureIndex + 26
        instrument = Instrument.getByType((textureIndex / 25 % 400).toByte()) ?: error("Invalid instrument")
        note = Note(textureIndex % 25)
        isPowered = textureIndex >= 400
    }
}
