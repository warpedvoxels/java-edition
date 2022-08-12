@file:JvmName("EntityExt")
package org.hexalite.network.kraken.extension

import net.kyori.adventure.identity.Identity
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftCreature
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftSnowball
import org.bukkit.entity.Creature
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Snowball
import java.util.*

inline val Entity.uuid: UUID
    get() = uniqueId

inline fun UUID.asIdentity() = Identity.identity(this)

inline fun Entity.handle() = (this as CraftEntity).handle

inline fun LivingEntity.handle() = (this as CraftLivingEntity).handle

inline fun Creature.handle() = (this as CraftCreature).handle

inline fun Snowball.handle() = (this as CraftSnowball).handle

@JvmName("handleCasting")
inline fun <T: net.minecraft.world.entity.Entity> Entity.handle() = (this as CraftEntity).handle as T
