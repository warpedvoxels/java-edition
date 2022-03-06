package org.hexalite.network.kraken.gameplay.features.blocks

import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun BlockData.textureIndex() = if (this is NoteBlock) ((instrument.type * 25) + note.id + (if (isPowered) 400 else 0) - 26) else null

inline fun CustomBlock.item(key: NamespacedKey) = ItemStack(Material.PAPER).apply {
    val meta = itemMeta ?: return this
    val container = meta.persistentDataContainer
    container.set(key, PersistentDataType.INTEGER, textureIndex)
    meta.setCustomModelData(textureIndex)
    meta.lore(mutableListOf<Component>(Component.text("§8#$textureIndex")))
    itemMeta = meta
}

inline fun CustomBlock.applyMetadataTo(block: Block) {
    block.setType(Material.NOTE_BLOCK, false)
    block.blockData = (Bukkit.createBlockData(Material.NOTE_BLOCK) as NoteBlock).apply {
        val textureIndex = textureIndex + 26
        instrument = Instrument.getByType((textureIndex / 25 % 400).toByte()) ?: error("Invalid instrument")
        note = Note(textureIndex % 25)
        isPowered = textureIndex >= 400
    }
}

context(CustomBlockAdapter)

fun ItemStack.asCustomBlockOrNull(namespace: NamespacedKey): CustomBlock? {
    if (type != Material.PAPER) {
        return null
    }
    val meta = itemMeta ?: return null
    val container = meta.persistentDataContainer
    return getter(container.get(namespace, PersistentDataType.INTEGER) ?: return null)
}
