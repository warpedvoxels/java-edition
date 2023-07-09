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

package net.warpedvoxels.command.paper

import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.commands.CommandSourceStack
import net.warpedvoxels.command.*
import net.warpedvoxels.core.architecture.PurpurExtension
import net.warpedvoxels.core.craftbukkit.CraftServer
import org.bukkit.command.Command

public typealias CommandContext =
        BrigadierCommandExecutionContext<CommandSourceStack>

/**
 * Respond a command with successful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.respond(text: String): Int = Success.also {
    source.sendSuccess(text)
}

/**
 * Respond a command with successful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.respond(text: Component): Int = Success.also {
    source.sendSuccess(text)
}

/**
 * Respond a command with unsuccessful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.fail(text: String): Nothing {
    source.sendFailure(net.minecraft.network.chat.Component.literal(text), true)
    throw CommandCancelFlowException
}

/**
 * Respond a command with unsuccessful feedback.
 * @param component The text component to be sent.
 */
public fun CommandContext.fail(component: Component): Nothing {
    source.sendFailure(PaperAdventure.asVanilla(component), true)
    throw CommandCancelFlowException
}

/**
 * Respond a command with unsuccessful feedback.
 * @param component The text component to be sent.
 */
public fun CommandContext.fail(
    component: net.minecraft.network.chat.Component
): Nothing {
    source.sendFailure(component, true)
    throw CommandCancelFlowException
}

/**
 * Registers a command without explicitly telling the plug-in
 * on its YAML configuration.
 * @param command The command to be registered.
 */
public fun PurpurExtension.registerCommand(command: Command): Boolean =
    (server as CraftServer).commandMap.register(namespace, command)

/**
 * Registers a command without explicitly telling the plug-in
 * on its YAML configuration.
 */
public fun PurpurExtension.registerCommand(
    command: BrigadierCommandDsl<CommandSourceStack>
): Boolean = registerCommand(
    BukkitBrigadierCommandWrapper(command)
)

/**
 * Registers a command without explicitly telling the plug-in
 * on its YAML configuration.
 */
context(PurpurExtension)
public operator fun Command.unaryPlus(): Boolean =
    registerCommand(this)

/**
 * Registers a command without explicitly telling the plug-in
 * on its YAML configuration.
 */
context(PurpurExtension)
public operator fun BrigadierCommandDsl<CommandSourceStack>.unaryPlus(): Boolean =
    registerCommand(
        BukkitBrigadierCommandWrapper(this)
    )


public typealias BrigadierDsl =
        BrigadierCommandDsl<CommandSourceStack>.() -> Unit

/**
 * A Kotlin DSL for Mojang's Brigadier library.
 * @param names Name and aliases of this command.
 */
@CommandDslMarker
public fun PurpurExtension.command(
    names: List<String>,
    permission: String? = null,
    treeApply: LiteralBuilderDsl<CommandSourceStack> = {},
    block: BrigadierDsl
): BrigadierCommandDsl<CommandSourceStack> {
    require(names.all(String::isNotEmpty)) {
        "Command name must be not empty."
    }
    return BrigadierCommandDsl(
        BrigadierCommandDefinition(
            names = names,
            permission = permission
        ),
        this,
        PaperCommandFrameworkPlatform,
        treeApply = treeApply
    ).apply(block)
}

/**
 * A Kotlin DSL for Mojang's Brigadier library.
 * @param name Name of this command.
 */
@CommandDslMarker
public fun PurpurExtension.command(
    name: String,
    permission: String? = null,
    treeApply: LiteralBuilderDsl<CommandSourceStack> = {},
    block: BrigadierDsl
): BrigadierCommandDsl<CommandSourceStack> {
    require(name.isNotEmpty()) { "Command name must be not empty." }
    return command(listOf(name), permission, treeApply, block)
}

