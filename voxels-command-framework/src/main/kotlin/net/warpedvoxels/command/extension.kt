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

@file:Suppress("SpellCheckingInspection")
@file:JvmName("CommandExtensions")

package net.warpedvoxels.command

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

public const val Success: Int = 1
public const val Failure: Int = -1

/** An exception that cancels the flow of a command. */
public data object CommandCancelFlowException :
    Exception(null, null, false, false) {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun baseThrow(key: String): Nothing {
    val component = Component.translatable(key)
    val plain = PlainTextComponentSerializer.plainText()
        .serialize(component)
    val type = SimpleCommandExceptionType { plain }
    throw type.create()
}

/**
 * Throws a built-in exception that indicates no players were
 * found.
 */
public fun noPlayersFound(): Nothing =
    baseThrow("argument.entity.notfound.player")

/**
 * Throws a built-in exception that indicates no entities were
 * found.
 */
public fun noEntitiesFound(): Nothing =
    baseThrow("argument.entity.notfound.entity")

/**
 * Throws a built-in exception that indicates the given selector
 * is not allowed.
 */
public fun selectorNotAllowed(): Nothing =
    baseThrow("argument.entity.selector.not_allowed")

/**
 * Throws a built-in exception that indicates only players are
 * accepted as argument.
 */
public fun onlyPlayersAllowed(): Nothing =
    baseThrow("argument.player.entities")

/**
 * Throws a built-in exception that indicates only a *single*
 * player is accepted.
 */
public fun notSinglePlayer(): Nothing =
    baseThrow("argument.player.toomany")

/**
 * Throws a built-in exception that indicates only a *single*
 * entity is accepted.
 */
public fun notSingleEntity(): Nothing =
    baseThrow("argument.entity.toomany")
