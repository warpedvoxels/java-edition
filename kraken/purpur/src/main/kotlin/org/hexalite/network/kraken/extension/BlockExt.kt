@file:JvmName("BlockExt")
package org.hexalite.network.kraken.extension

import net.minecraft.core.BlockPos
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock

inline fun Block.handle() = (this as CraftBlock).position

inline fun Block.entityId(): Int = handle().entityId()

fun BlockPos.entityId(): Int = ((x and 0xFFF) shl 20) or ((z and 0xFFF) shl 8) or (y and 0xFF)
