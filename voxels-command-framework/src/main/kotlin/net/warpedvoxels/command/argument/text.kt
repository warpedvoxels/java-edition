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

package net.warpedvoxels.command.argument

import com.mojang.brigadier.arguments.StringArgumentType
import net.warpedvoxels.command.CommandArgument
import net.warpedvoxels.command.CommandArgumentScope
import net.warpedvoxels.command.CommandDslMarker
import net.warpedvoxels.command.SuggestionsDsl

@CommandDslMarker
public fun <S> CommandArgumentScope<S>.literal(
    name: String,
    type: StringArgumentType,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, String, String> =
    argument(name, type, StringArgumentType::getString, suggestions)

@CommandDslMarker
public fun <S> CommandArgumentScope<S>.word(
    name: String,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, String, String> =
    literal(name, StringArgumentType.word(), suggestions)

@CommandDslMarker
public fun <S> CommandArgumentScope<S>.string(
    name: String,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, String, String> =
    literal(name, StringArgumentType.string(), suggestions)


@CommandDslMarker
public fun <S> CommandArgumentScope<S>.greedyString(
    name: String,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, String, String> =
    literal(name, StringArgumentType.greedyString(), suggestions)