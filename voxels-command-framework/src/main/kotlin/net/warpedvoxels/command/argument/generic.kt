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

@file:JvmName("GenericArgument")

package net.warpedvoxels.command.argument

import com.mojang.brigadier.arguments.ArgumentType
import kotlinx.coroutines.future.await
import net.warpedvoxels.command.*

@CommandDslMarker
public fun <S, T : Any?, B> CommandArgumentScope<S>.argument(
    name: String,
    type: ArgumentType<B>,
    getter: ArgumentGetter<S, T>,
    suggestions: SuggestionsDsl<S>? = null,
): CommandArgument<S, T, B> = CommandArgument.Required(
    name, type, tree.coroutineScope,
    if (suggestions == null) null else { builder ->
        val dsl = SuggestionsBuilderDsl(this, builder)
        dsl.suggestions()
        builder.buildFuture().await()
    }, getter
)

///**
// * Registers this command argument and makes it mandatory.
// */
//context(BrigadierCommandDsl<S>)
//public fun <S, T, B> CommandArgument<S, T, B>.required():
//        CommandArgument<S, T, B> = tree.argument(this)

/**
 * Registers this command argument and makes it optional.
 * @param default The default value for this property.
 */
context(BrigadierCommandDsl<S>)
@CommandDslMarker
public fun <S, T, B> CommandArgument<S, T, B>.optional(
    default: ArgumentGetter<S, T?> = { null },
): CommandArgument<S, T?, B> =
    CommandArgument.Optional(this, default)
//tree.argument(CommandArgument.Optional(this, default))