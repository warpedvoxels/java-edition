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

package net.warpedvoxels.command.velocity

import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.warpedvoxels.command.*
import net.warpedvoxels.proxy.core.VelocityExtension

public typealias CommandContext =
        BrigadierCommandExecutionContext<CommandSource>

/**
 * Respond a command with successful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.respond(text: String): Int =
    respond(Component.text(text))

/**
 * Respond a command with successful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.respond(text: Component): Int = Success.also {
    source.sendMessage(text)
}

/**
 * Respond a command with unsuccessful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.fail(text: String): Nothing =
    fail(Component.text(text))

/**
 * Respond a command with unsuccessful feedback.
 * @param component The text component to be sent.
 */
public fun CommandContext.fail(component: Component): Nothing {
    val out = Component.text().color(NamedTextColor.RED)
        .append(component)
    source.sendMessage(out)
    throw CommandCancelFlowException
}

/**
 * Registers a command without explicitly telling the plug-in
 * on its YAML configuration.
 */
public fun VelocityExtension.registerCommand(
    command: BrigadierCommandDsl<CommandSource>
) {
    val cmd = BrigadierCommand(command.tree.build())
    val meta = proxyServer.commandManager.metaBuilder(cmd)
        .aliases(*command.definition.names.drop(1).toTypedArray())
    proxyServer.commandManager.register(meta.build(), cmd)
}

context(VelocityExtension)
public operator fun BrigadierCommandDsl<CommandSource>.unaryPlus(): Unit =
    registerCommand(this)


public typealias BrigadierDsl =
        BrigadierCommandDsl<CommandSource>.() -> Unit

/**
 * A Kotlin DSL for Mojang's Brigadier library.
 * @param names Name and aliases of this command.
 */
@CommandDslMarker
public fun VelocityExtension.command(
    names: List<String>,
    permission: String? = null,
    treeApply: LiteralBuilderDsl<CommandSource> = {},
    block: BrigadierDsl
): BrigadierCommandDsl<CommandSource> {
    require(names.all(String::isNotEmpty)) {
        "Command name must be not empty."
    }
    return BrigadierCommandDsl(
        BrigadierCommandDefinition(
            names = names,
            permission = permission
        ),
        this,
        VelocityCommandFrameworkPlatform(this),
        treeApply
    ).apply(block)
}

/**
 * A Kotlin DSL for Mojang's Brigadier library.
 * @param name Name of this command.
 */
@CommandDslMarker
public fun VelocityExtension.command(
    name: String,
    permission: String? = null,
    treeApply: LiteralBuilderDsl<CommandSource> = {},
    block: BrigadierDsl
): BrigadierCommandDsl<CommandSource> {
    require(name.isNotEmpty()) { "Command name must be not empty." }
    return command(listOf(name), permission, treeApply, block)
}

