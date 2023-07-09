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

@file:JvmName("BrigadierCommandDSL")

package net.warpedvoxels.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import kotlinx.coroutines.CoroutineScope
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import com.mojang.brigadier.Command as BrigadierCommand

@DslMarker
@Target(AnnotationTarget.FUNCTION)
public annotation class CommandDslMarker

public typealias CommandExec<S> = BrigadierCommandExecutionContext<S>.
    () -> Int


public typealias CommandUExec<S> = BrigadierCommandExecutionContext<S>.
    () -> Unit

public typealias LiteralBuilderDsl<S> = LiteralArgumentBuilder<S>.
    () -> Unit

public typealias SyntaxErrorHandler<S> = BrigadierCommandExecutionContext<S>.
    (Exception) -> Unit

/**
 * The context for the ongoing process of a command execution.
 * @param ctx The original context provided by Brigadier.
 * @param S   The type for command sources.
 */
@JvmInline
public value class BrigadierCommandExecutionContext<S>(
    public val ctx: CommandContext<S>
) {
    public inline val source: S
        get() = ctx.source
}

public class BrigadierCommandDsl<S>(
    public val definition: BrigadierCommandDefinition,
    coroutineScope: CoroutineScope,
    platform: CommandFrameworkPlatform<S>,
    treeApply: LiteralBuilderDsl<S> = {},
) {
    public val tree: BrigadierCommandTree<S> = BrigadierCommandTree(
        definition.names.first().lowercase(Locale.ENGLISH),
        definition.permission,
        coroutineScope,
        platform,
        null,
        treeApply
    )

    public val args: CommandArgumentScope<S>
        get() =
            tree.args

    /**
     * Defines the execution callback after all children are
     * processed.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public fun runs(command: CommandExec<S>) {
        contract {
            callsInPlace(command, InvocationKind.AT_LEAST_ONCE)
        }
        tree.runs(command)
    }

    /**
     * Defines the execution callback after all children are
     * processed.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public fun executes(command: CommandUExec<S>) {
        contract {
            callsInPlace(command, InvocationKind.AT_LEAST_ONCE)
        }
        tree.executes(command)
    }

    /**
     * Registers a subcommand into this tree.
     * @param name The name of the subcommand.
     * @param block The DSL callback for the subcommand.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public fun subcommand(
        name: String,
        permission: String? = definition.permission,
        keepArgs: Boolean = true,
        block: LiteralBuilderDsl<S> = {},
        apply: TreeBuilderDsl<S>,
    ): BrigadierCommandTree<S> {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        return tree.subcommand(name, permission, keepArgs, block, apply)
    }

    public fun build(): List<LiteralCommandNode<S>> {
        val head = tree.build().build()
        val result = mutableListOf(head)
        definition.names.drop(1).forEach {
            val alias = LiteralArgumentBuilder
                .literal<S>(it.lowercase(Locale.ENGLISH))
                .requires(head.requirement)
                .forward(
                    head.redirect,
                    head.redirectModifier,
                    head.isFork
                )
                .executes(head.command)
            head.children.forEach { child ->
                alias.then(child)
            }
            result.add(alias.build())
        }
        return result
    }

    // argument delegate provider
    public operator fun <T, B> CommandArgument<S, T, B>.provideDelegate(
        ref: Any?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Any?, T> =
        with(tree) {
            provideDelegate(ref, prop)
        }
}

public typealias TreeBuilderDsl<S> = BrigadierCommandTree<S>.
    () -> Unit

/**
 * Builds a tree that follows the subcommand and argument order of
 * a command defined on Mojang's Brigadier command library.
 */
public class BrigadierCommandTree<S>(
    private val name: String,
    private val permission: String?,
    public val coroutineScope: CoroutineScope,
    public val platform: CommandFrameworkPlatform<S>,
    private val parent: AtomicReference<BrigadierCommandTree<S>>? = null,
    private val apply: LiteralBuilderDsl<S> = {}
) {
    private var context: BrigadierCommandExecutionContext<S>? = null
    private var execute: CommandExec<S>? = null

    private val arguments: MutableSet<CommandArgument<S, Any?, Any?>> =
        linkedSetOf()

    private val subcommands: MutableList<BrigadierCommandTree<S>> =
        mutableListOf()

    public val args: CommandArgumentScope<S> =
        CommandArgumentScope(this)

    /**
     * Defines the execution callback after all children are
     * processed.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public fun runs(command: CommandExec<S>) {
        contract {
            callsInPlace(command, InvocationKind.AT_LEAST_ONCE)
        }
        this.execute = command
    }

    /**
     * Defines the execution callback after all children are
     * processed.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public fun executes(command: CommandUExec<S>) {
        contract {
            callsInPlace(command, InvocationKind.AT_LEAST_ONCE)
        }
        runs {
            command()
            BrigadierCommand.SINGLE_SUCCESS
        }
    }

    /**
     * Registers a child argument into this tree.
     * @param child The argument to be registered.
     */
    @Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
    public fun <T : Any?, B : Any?> argument(
        child: CommandArgument<S, T, B>
    ): CommandArgument<S, T, B> = child.also {
        val last = arguments.lastOrNull()
        if (last != null && last is CommandArgument.Optional) {
            throw IllegalArgumentException("Cannot register required argument after an optional one.")
        }
        arguments.add(it as CommandArgument<S, Any?, Any?>)
    }

    /**
     * Registers a subcommand into this tree.
     * @param name The name of the subcommand.
     * @param block The DSL callback for the subcommand.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public fun subcommand(
        name: String,
        permission: String? = this.permission,
        keepArgs: Boolean = true,
        block: LiteralBuilderDsl<S> = {},
        apply: TreeBuilderDsl<S>
    ): BrigadierCommandTree<S> {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        return BrigadierCommandTree(
            name, permission, coroutineScope, platform, if (keepArgs)
                AtomicReference(this) else null, block
        ).apply(apply).also {
            subcommands.add(it)
        }
    }

    // argument delegate provider
    public operator fun <T, B> CommandArgument<S, T, B>.provideDelegate(
        ref: Any?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Any?, T> {
        argument(this)
        return ReadOnlyProperty { _, _ ->
            if (context == null) {
                throw IllegalAccessException("Cannot access argument outside of commands.")
            }
            get(context!!.ctx, prop)
        }
    }

    private fun setContext(ctx: CommandContext<S>) {
        context = BrigadierCommandExecutionContext(ctx)
        var parent: BrigadierCommandTree<S>? = parent?.get()
        while (parent != null) {
            parent.context = context
            parent = parent.parent?.get()
        }
    }

    /** Builds this tree into a [LiteralArgumentBuilder]. */
    public fun build(): LiteralArgumentBuilder<S> {
        val node = LiteralArgumentBuilder.literal<S>(name).apply(apply)
        if (permission != null) {
            node.requires {
                platform.hasPermission(it, permission)
            }
        }
        val run = Command { ctx ->
            setContext(ctx)
            if (execute != null) {
                try {
                    execute!!(context!!)
                } catch (_: CommandCancelFlowException) {
                    -1
                }
            } else Command.SINGLE_SUCCESS
        }
        val arguments = if (parent != null)
            parent.get().arguments + arguments
        else
            this.arguments
        if (arguments.isEmpty()) {
            node.executes(run)
        } else {
            val beginsOptional = arguments.indexOfFirst { it is CommandArgument.Optional }
            val nodes = arguments.map(CommandArgument<S, *, *>::brigadier)
            if (beginsOptional != -1) {
                for (index in beginsOptional..<nodes.size) {
                    nodes[index].executes(run)
                }
                val previous = nodes.getOrNull(beginsOptional - 1)
                (previous ?: node).executes(run)
            } else {
                nodes.last().executes(run)
            }
            if (nodes.size > 1) {
                for (index in nodes.lastIndex downTo 1) {
                    val `this` = nodes[index]
                    val lookbehind = nodes[index - 1]
                    lookbehind.then(`this`)
                }
            }
            node.then(nodes.first().build())
        }
        subcommands.forEach {
            node.then(it.build().build())
        }
        return node
    }
}

public typealias SuggestionsDsl<S> = SuggestionsBuilderDsl<S>.() -> Unit

/**
 * Simple wrapper class around command suggestion building.
 */
public data class SuggestionsBuilderDsl<S>(
    val ctx: CommandContext<S>,
    val builder: SuggestionsBuilder
) {
    public fun suggest(vararg values: Any): Unit = values.forEach {
        when (it) {
            is Int -> builder.suggest(it)
            is SuggestionsBuilder -> builder.add(it)
            else -> builder.suggest(it.toString())
        }
    }
}


/**
 * Argument creation scope.
 * @param S The type for command sources.
 */
@JvmInline
public value class CommandArgumentScope<S>(
    public val tree: BrigadierCommandTree<S>
)
