package org.hexalite.network.kraken.extension

import net.minecraft.core.BlockPos
import org.bukkit.block.Block

fun Block.entityId(): Int = ((x and 0xFFF) shl 20) or ((z and 0xFFF) shl 8) or (y and 0xFF)

fun BlockPos.entityId(): Int = ((x and 0xFFF) shl 20) or ((z and 0xFFF) shl 8) or (y and 0xFF)
