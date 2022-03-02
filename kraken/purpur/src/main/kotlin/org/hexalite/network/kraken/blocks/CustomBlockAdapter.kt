package org.hexalite.network.kraken.blocks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import org.bukkit.GameMode
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.entity.Item
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.collections.onlinePlayersSetOf
import org.hexalite.network.kraken.coroutines.BukkitAsync
import org.hexalite.network.kraken.coroutines.launch
import org.hexalite.network.kraken.coroutines.ticks
import org.hexalite.network.kraken.extension.BukkitEventListener

class CustomBlockAdapter(internal val getter: (Int) -> CustomBlock?, override val plugin: KrakenPlugin): BukkitEventListener {
    val ID = NamespacedKey(plugin, "id")

    private val fastPlaceExempt = plugin.onlinePlayersSetOf()

    @Suppress("DEPRECATION")
    private operator fun get(block: NoteBlock): CustomBlock? {
        val id = (block.instrument.type * 25) + block.note.id + (if (block.isPowered) 400 else 0) - 26
        println("ID: $id")
        return getter(id)
    }

    private tailrec fun update(above: Block) {
        above.state.update(true, true)
        val next = above.location.block.getRelative(BlockFace.UP)
        if (next.type == Material.NOTE_BLOCK) {
            update(next)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun readBlockPhysics(event: BlockPhysicsEvent) = with(event) {
        val above = block.getRelative(BlockFace.UP)
        if (above.type == Material.NOTE_BLOCK) {
            update(above)
            isCancelled = true
        }
        if (block.type == Material.NOTE_BLOCK) {
            isCancelled = true
            block.state.update(true, false)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun readPistonExtend(event: BlockPistonExtendEvent) = with(event) {
        if (blocks.any { it.type == Material.NOTE_BLOCK }) {
            isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun readPistonRetract(event: BlockPistonRetractEvent) = with(event) {
        if (blocks.any { it.type == Material.NOTE_BLOCK }) {
            isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun readNotePlay(event: NotePlayEvent) = with(event) {
        if (instrument != Instrument.PIANO) {
            isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun readBlockBreak(event: BlockBreakEvent) = with(event) {
        if (block.type != Material.NOTE_BLOCK || !event.isDropItems) {
            return
        }
        val noteBlock = block.blockData as NoteBlock
        val customBlock = get(noteBlock) ?: return
        if (customBlock.breakSound != null) {
            block.world.playSound(block.location, customBlock.breakSound, 1f, 0.8f)
        }
        isDropItems = false
        expToDrop = 0
        customBlock.onDrop(this, customBlock, this@CustomBlockAdapter)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun readPlayerInteract(event: PlayerInteractEvent): Unit = with(event) {
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (clickedBlock?.type == Material.NOTE_BLOCK && !player.isSneaking) {
            return event.setCancelled(true)
        }
        if (player in fastPlaceExempt) {
            return
        }
        val custom = player.inventory.itemInMainHand.asCustomBlock()?.to(player.inventory.itemInMainHand)
            ?: player.inventory.itemInOffHand.asCustomBlock()?.to(player.inventory.itemInOffHand)
            ?: return

        val block = clickedBlock?.getRelative(blockFace) ?: return
        val slot = if (custom.second == player.inventory.itemInMainHand) EquipmentSlot.HAND else EquipmentSlot.OFF_HAND

        runCatching {
            if (!BlockPlaceEvent(block, block.state, clickedBlock!!, item!!, player, true, slot).callEvent()) {
                return
            }
        }
        if (block.world.getNearbyEntities(block.location.add(.5, .5, .5), .5, .5, .5) { it !is Item && it !is ItemFrame }.isNotEmpty()) {
            return
        }

        if (slot == EquipmentSlot.HAND) {
            player.swingMainHand()
        } else {
            player.swingOffHand()
        }
        block.setType(Material.NOTE_BLOCK, false)
        custom.first.applyMetadataTo(block)

        if (player.gameMode != GameMode.CREATIVE) {
            item!!.amount--
        }
        if (item!!.amount < 0) {
            item!!.type = Material.AIR
        }

        fastPlaceExempt.add(player)
        plugin.launch(Dispatchers.BukkitAsync) {
            delay(2.ticks)
            fastPlaceExempt.remove(player)
        }
    }

}