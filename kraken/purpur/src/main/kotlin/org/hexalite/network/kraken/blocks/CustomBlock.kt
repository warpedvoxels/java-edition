package org.hexalite.network.kraken.blocks

import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

open class CustomBlock(
    val textureIndex: Int,
    val onDrop: BlockBreakEvent.(block: CustomBlock, adapter: CustomBlockAdapter) -> Unit = { custom, adapter ->
        block.world.dropItemNaturally(block.location.add(.5, .5, .5), custom.item(adapter.ID))
    },
    val hardness: Int? = null,
    val breakSound: String? = null,
)

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
        println("""
            |Instrument: ${instrument.name}
            |Note: ${note.id}
            |Powered: $isPowered
        """.trimIndent()
        )
    }
}

context(CustomBlockAdapter)

fun ItemStack.asCustomBlock(): CustomBlock? {
    if (type != Material.PAPER) {
        return null
    }
    val meta = itemMeta ?: return null
    val container = meta.persistentDataContainer
    return getter(container.get(ID, PersistentDataType.INTEGER) ?: return null)
}
