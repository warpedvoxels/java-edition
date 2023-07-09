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

package net.warpedvoxels.core.architecture

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public typealias LifecycleInitialStateListener = () -> Unit

public typealias LifecyclePropertyInitializer<T> = () -> T

public typealias LifecyclePropertyReceiver<T> = (T) -> Unit

@Suppress("MemberVisibilityCanBePrivate")
public data class PluginLifecycleProperty<T>(
    val priority: Int,
    val listeners: PluginLifecyclePropertyListeners<T>,
) : Comparable<PluginLifecycleProperty<*>> {
    internal val value by lazy(listeners.enable)

    public var isReady: Boolean = false
        internal set

    override fun compareTo(other: PluginLifecycleProperty<*>): Int =
        priority.compareTo(other.priority)
}

public data class PluginLifecyclePropertyListeners<T>(
    // val load: LifecycleInitialStateListener?,
    val enable: LifecyclePropertyInitializer<T>,
    val disable: LifecyclePropertyReceiver<T>
)

public class PluginLifecycleDelegatedProperty<T>(
    override val extension: PurpurExtension,
    private val property: PluginLifecycleProperty<T>,
) : ReadOnlyProperty<Any?, T>, UsesExtension {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!this.property.isReady) {
            throw IllegalAccessException("Property not ready yet.")
        }
        return this.property.value
    }
}

/**
 * Lazily initialized property, available once the extension is
 * enabled.
 * @param priority The priority this property would be initialized
 *                 first other than the other lazy properties.
 * @param init     Initialization hook, called when the extension is
 *                 enabled.
 * @param uninit   Initialization hook, called when the extension is
 *                 disabled, turns the property inaccessible as well.
 */
@Suppress("UNCHECKED_CAST")
public fun <T : Any> PurpurExtension.lifecycle(
    priority: Int = 0,
    init: LifecyclePropertyInitializer<T>,
    uninit: LifecyclePropertyReceiver<T>
): PluginLifecycleDelegatedProperty<T> {
    val listeners = PluginLifecyclePropertyListeners(init, uninit)
    val lifecycle = PluginLifecycleProperty(priority, listeners).also {
        lifecycles.add(it as PluginLifecycleProperty<Any>)
    }
    return PluginLifecycleDelegatedProperty(this, lifecycle)
}