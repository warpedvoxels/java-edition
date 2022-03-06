package org.hexalite.network.kraken.gameplay.feature.block

import org.bukkit.Bukkit
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.Note
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.NoteBlock
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureView

fun BlockData.textureIndex() = if (this is NoteBlock) ((instrument.type * 25) + note.id + (if (isPowered) 400 else 0) - 26) else null

inline fun CustomBlockFeature.stack(view: GameplayFeatureView, amount: Int = 1, showTextureIndexInTheLore: Boolean = true) =
    view.retrieveCustomItem(textureIndex)?.stack(view.id, amount, showTextureIndexInTheLore)

inline fun CustomBlockFeature.applyMetadataTo(block: Block) {
    block.setType(Material.NOTE_BLOCK, false)
    block.blockData = (Bukkit.createBlockData(Material.NOTE_BLOCK) as NoteBlock).apply {
        val textureIndex = textureIndex + 26
        instrument = Instrument.getByType((textureIndex / 25 % 400).toByte()) ?: error("Invalid instrument")
        note = Note(textureIndex % 25)
        isPowered = textureIndex >= 400
    }
}
