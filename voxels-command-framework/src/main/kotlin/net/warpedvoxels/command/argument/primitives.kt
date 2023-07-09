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

@file:JvmName("NumberArgument")

package net.warpedvoxels.command.argument

import com.mojang.brigadier.arguments.*
import net.warpedvoxels.command.CommandArgument
import net.warpedvoxels.command.CommandArgumentScope
import net.warpedvoxels.command.CommandDslMarker
import net.warpedvoxels.command.SuggestionsDsl

@CommandDslMarker
public fun <S> CommandArgumentScope<S>.integer(
    name: String,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Int, Int> = argument(
    name,
    IntegerArgumentType.integer(min, max),
    IntegerArgumentType::getInteger,
    suggestions
)

@CommandDslMarker
public fun <S> CommandArgumentScope<S>.long(
    name: String,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Long, Long> = argument(
    name,
    LongArgumentType.longArg(min, max),
    LongArgumentType::getLong,
    suggestions
)

@CommandDslMarker
public fun <S> CommandArgumentScope<S>.double(
    name: String,
    min: Double = Double.MIN_VALUE,
    max: Double = Double.MAX_VALUE,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Double, Double> = argument(
    name,
    DoubleArgumentType.doubleArg(min, max),
    DoubleArgumentType::getDouble,
    suggestions
)

@CommandDslMarker
public fun <S> CommandArgumentScope<S>.float(
    name: String,
    min: Float = Float.MIN_VALUE,
    max: Float = Float.MAX_VALUE,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Float, Float> = argument(
    name,
    FloatArgumentType.floatArg(min, max),
    FloatArgumentType::getFloat,
    suggestions
)

@CommandDslMarker
public fun <S> CommandArgumentScope<S>.bool(
    name: String,
    suggestions: SuggestionsDsl<S>? = null
): CommandArgument<S, Boolean, Boolean> =
    argument(
        name,
        BoolArgumentType.bool(),
        BoolArgumentType::getBool,
        suggestions
    )