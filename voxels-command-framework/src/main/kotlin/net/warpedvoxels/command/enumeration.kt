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

@file:JvmName("CommandEnumSubcommand")

package net.warpedvoxels.command

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public fun String.snakecase(): String = split("([a-z])([A-Z]+)".toRegex())
    .joinToString("_")
    .lowercase()

public fun <E : Enum<E>> Enum<E>.lowercase(): String = name.lowercase()

public fun <E : Enum<E>> Enum<E>.uppercase(): String = name.uppercase()

public fun <E : Enum<E>> Enum<E>.snakecase(): String = name.snakecase()

public typealias EnumCommandUExec<S, E> = BrigadierCommandExecutionContext<S>.
    (literal: E, name: String) -> Int

@OptIn(ExperimentalContracts::class)
@CommandDslMarker
public inline fun <reified E : Enum<E>, S> CommandArgumentScope<S>.enumeration(
    name: (E) -> String = Enum<E>::lowercase,
    permission: (E) -> String? = { null },
    keepArgs: Boolean = true,
    crossinline block: EnumCommandUExec<S, E>
) {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }
    enumValues<E>().forEach { value ->
        val transformedName = name(value)
        tree.subcommand(transformedName, permission(value), keepArgs) {
            runs {
                block(value, transformedName)
            }
        }
    }
}