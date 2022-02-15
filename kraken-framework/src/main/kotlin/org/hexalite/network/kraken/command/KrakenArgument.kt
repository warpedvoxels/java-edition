package org.hexalite.network.kraken.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext

typealias ArgumentGetter<S, T> =
        (CommandContext<S>, String) -> T

interface KrakenArgument<S, T, V> {
    val name: String
    val type: ArgumentType<T>
    val command: KrakenCommand<S>

    /**
     * Get the argument value in the given [context]. Function is called `getValue` instead of only `value` because
     * it is the only way to create a delegated variable.
     * @param context the context to get the argument value in.
     * @return the argument value.
     */
    operator fun get(context: CommandContext<S>): V

    /**
     * Creates a brigadier argument from this command argument.
     * @return the brigadier argument.
     */
    fun brigadier(): RequiredArgumentBuilder<S, T>

    /**
     * A mandatory command argument used for commands in the Kraken Framework.
     */
    data class Required<S, T, V>(
        override val name: String,
        override val command: KrakenCommand<S>,
        override val type: ArgumentType<T>,
        val getter: ArgumentGetter<S, V>
    ) : KrakenArgument<S, T, V> {
        override operator fun get(context: CommandContext<S>): V = getter(context, name)

        override fun brigadier(): RequiredArgumentBuilder<S, T> = RequiredArgumentBuilder.argument(name, type)
    }

    /**
     * An optional command argument used for commands in the Kraken Framework.
     */
    data class Optional<S, T, V>(
        val required: Required<S, T, V>,
        override val type: ArgumentType<T>,
        val default: ArgumentGetter<S, V?> = { _, _ -> null }
    ) : KrakenArgument<S, T, V?> {
        override val name: String
            get() = required.name

        override val command: KrakenCommand<S>
            get() = required.command

        override operator fun get(context: CommandContext<S>) = runCatching { required[context] }.getOrElse { default(context, name) }

        override fun brigadier(): RequiredArgumentBuilder<S, T> =
            RequiredArgumentBuilder.argument(name, type)
    }
}
