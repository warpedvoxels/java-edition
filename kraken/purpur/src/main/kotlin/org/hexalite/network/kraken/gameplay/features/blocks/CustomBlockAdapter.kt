package org.hexalite.network.kraken.gameplay.features.blocks

import kotlinx.coroutines.delay
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import org.bukkit.GameMode
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.entity.Item
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType.SLOW_DIGGING
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.scheduler
import org.hexalite.network.kraken.collections.onlinePlayersSetOf
import org.hexalite.network.kraken.coroutines.BukkitDispatchers
import org.hexalite.network.kraken.coroutines.launchCoroutine
import org.hexalite.network.kraken.coroutines.ticks
import org.hexalite.network.kraken.extension.BukkitEventListener
import org.hexalite.network.kraken.extension.entityId
import org.hexalite.network.kraken.extension.findPlayerOrNull
import org.hexalite.network.kraken.gameplay.features.GameplayFeaturesView
import org.hexalite.network.kraken.logging.warning
import org.hexalite.network.kraken.pipeline.packet.BukkitPacketPipelineListener
import org.hexalite.network.kraken.pipeline.packet.sendPacket
import org.hexalite.network.kraken.pipeline.packet.transformPacketsIncoming
import org.hexalite.network.kraken.pipeline.packet.uuidOrNull

class CustomBlockAdapter(override val plugin: KrakenPlugin, val view: GameplayFeaturesView, internal val getter: (Int) -> CustomBlock?): BukkitEventListener {
    private val fastPlaceExempt = plugin.onlinePlayersSetOf()
    private val breakingBlocks = plugin.onlinePlayersSetOf()

    init {
        if (HandlerList.getRegisteredListeners(plugin).any { it.listener is BukkitPacketPipelineListener }) {
            setupHardnessPacketInjection()
        } else {
            plugin.log.warning { "The packet pipeline injection system is not registered. This is required for custom block hardness support." }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun readBlockPhysics(event: BlockPhysicsEvent) = with(event) {
        val above = block.getRelative(BlockFace.UP)
        if (above.type == Material.NOTE_BLOCK) {
            tailrec fun update(above: Block) {
                above.state.update(true, true)
                val next = above.location.block.getRelative(BlockFace.UP)
                if (next.type == Material.NOTE_BLOCK) {
                    update(next)
                }
            }
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
    fun readBlockBreak(event: BlockBreakEvent): Unit = with(event) {
        if (block.type != Material.NOTE_BLOCK || !event.isDropItems) {
            return
        }
        val noteBlock = block.blockData as NoteBlock
        val customBlock = getter(noteBlock.textureIndex() ?: return) ?: return
        if (customBlock.breakSound != null) {
            block.world.playSound(block.location, customBlock.breakSound!!, 1f, 0.8f)
        }
        isDropItems = false
        expToDrop = 0
        if (customBlock.onDrop != null) {
            val drop = customBlock.onDrop!!(this, customBlock, this@CustomBlockAdapter)
            if (drop != null) {
                block.world.dropItemNaturally(block.location.add(.5, .5, .5), drop)
            }
        }
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

        val custom = player.inventory.itemInMainHand.asCustomBlockOrNull(view.id)?.to(player.inventory.itemInMainHand)
            ?: player.inventory.itemInOffHand.asCustomBlockOrNull(view.id)?.to(player.inventory.itemInOffHand)
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

        if (custom.first.placeSound != null) {
            player.world.playSound(block.location, custom.first.placeSound!!, 10f, 10f)
        }

        if (player.gameMode != GameMode.CREATIVE) {
            item!!.amount--
        }
        if (item!!.amount < 0) {
            item!!.type = Material.AIR
        }

        fastPlaceExempt.add(player)
        plugin.launchCoroutine(BukkitDispatchers::Async) {
            delay(2.ticks)
            fastPlaceExempt.remove(player)
        }
    }

    private inline fun setupHardnessPacketInjection() = transformPacketsIncoming<ServerboundPlayerActionPacket> { ctx, packet ->
        val player = uuidOrNull?.findPlayerOrNull()
        if (player == null || player.gameMode == GameMode.CREATIVE) {
            return@transformPacketsIncoming packet
        }
        val block = player.world.getBlockAt(packet.pos.x, packet.pos.y, packet.pos.z)
        val custom = getter(block.blockData.textureIndex() ?: return@transformPacketsIncoming packet) ?: return@transformPacketsIncoming packet
        if (custom.hardness == null) {
            return@transformPacketsIncoming packet
        }
        val entityId = block.entityId()
        if (packet.action == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            val speed = custom.hardness!!(custom, player)
            val oldSlowDiggingEffect = player.activePotionEffects.firstOrNull { it.type == SLOW_DIGGING }
            plugin.launchCoroutine(BukkitDispatchers::Main) {
                player.addPotionEffect(PotionEffect(SLOW_DIGGING, speed * 11, Int.MAX_VALUE, false, false, false))
            }
            val delay = speed.toLong()
            breakingBlocks.add(player)

            var value = 0
            scheduler.runTaskTimer(plugin, timer@{ task ->
                if (player !in breakingBlocks) {
                    player.removePotionEffect(SLOW_DIGGING)
                    if (oldSlowDiggingEffect != null) {
                        player.addPotionEffect(oldSlowDiggingEffect)
                    }
                    return@timer task.cancel()
                }
                if (value++ < 10) {
                    for (p in block.world.getNearbyPlayers(block.location, 16.0)) {
                        p.sendPacket(ClientboundBlockDestructionPacket(entityId, packet.pos, value))
                    }
                    return@timer
                } else {
                    for (p in block.world.getNearbyPlayers(block.location, 16.0)) {
                        p.sendPacket(ClientboundBlockDestructionPacket(entityId, packet.pos, 10))
                    }
                    val state = runCatching { BlockBreakEvent(block, player).callEvent() }.getOrNull() ?: true
                    if (state) {
                        block.type = Material.AIR
                        PlayerItemDamageEvent(player, player.inventory.itemInMainHand, 1).callEvent()
                    }
                    breakingBlocks.remove(player)
                }
            }, delay, delay)
        } else {
            scheduler.runTask(plugin) { _ ->
                for (p in block.world.getNearbyPlayers(block.location, 16.0)) {
                    p.sendPacket(ClientboundBlockDestructionPacket(entityId, packet.pos, 10))
                }
            }
            breakingBlocks.remove(player)
        }
        null
    }

}