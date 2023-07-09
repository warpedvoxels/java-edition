/*
 * WarpedVoxels, a network of Minecraft: Java Edition servers
 * Copyright (C) 2023  Pedro Henrique
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.warpedvoxels.core.utility.extension

import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.WritableRegistry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.warpedvoxels.core.craftbukkit.CraftServer
import org.bukkit.Bukkit
import java.lang.reflect.Field
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@DslMarker
@Target(AnnotationTarget.FUNCTION)
public annotation class RegistryDsl

@PublishedApi
internal val frozen: Field
    get() = try {
        MappedRegistry::class.java.getDeclaredField("frozen")
            .apply {
                isAccessible = true
            }
    } catch (notFound: NoSuchFieldException) {
        throw IllegalStateException(
            "Cannot unfreeze registry, property not found", notFound
        )
    }

public fun <T> registryOrThrow(key: ResourceKey<out Registry<out T>>): Registry<T> =
    (Bukkit.getServer() as CraftServer).handle.server.registryAccess()
        .registryOrThrow(key)

public fun <V : Any, T : V> Registry<V>.register(
    location: ResourceLocation, entry: T
): T = Registry.register(this, location, entry)

@OptIn(ExperimentalContracts::class)
@RegistryDsl
public inline fun <T, R> Registry<T>.use(block: WritableRegistry<T>.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val writable = this as WritableRegistry<T>
    frozen[writable] = false
    val result = writable.block()
    frozen[writable] = true
    return result
}