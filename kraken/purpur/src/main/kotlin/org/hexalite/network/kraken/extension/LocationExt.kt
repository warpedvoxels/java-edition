@file:JvmName("LocationExt")
package org.hexalite.network.kraken.extension

import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld

//    __                 __  _
//   / /  ___  _______ _/ /_(_)__  ___
//  / /__/ _ \/ __/ _ `/ __/ / _ \/ _ \
// /____/\___/\__/\_,_/\__/_/\___/_//_/

inline fun Location.component1() = x

inline fun Location.component2() = y

inline fun Location.component3() = z

inline fun Location.getInteractionPoint(maxDistance: Double, ignorePassableBlocks: Boolean = true): Location? {
    if (world == null) {
        return null
    }
    val result = world.rayTraceBlocks(this, direction, maxDistance, FluidCollisionMode.NEVER, ignorePassableBlocks)
    if (result == null || result.hitBlock == null) {
        return null
    }
    return result.hitPosition.subtract(result.hitBlock!!.location.toVector()).toLocation(world)
}

inline fun World.handle() = (this as CraftWorld).handle

