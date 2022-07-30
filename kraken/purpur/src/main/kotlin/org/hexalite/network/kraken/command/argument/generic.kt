package org.hexalite.network.kraken.command.argument

import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.CommandSourceStack
import org.hexalite.network.kraken.command.ArgumentGetter
import org.hexalite.network.kraken.command.KrakenArgument
import org.hexalite.network.kraken.command.KrakenCommand
import org.hexalite.network.kraken.command.SuggestionsProvider
import org.hexalite.network.kraken.command.dsl.KrakenDslCommand
import org.hexalite.network.kraken.command.dsl.SuggestionsDsl

fun <S, T : Any?, V> KrakenCommand<S>.createArgument(
    name: String,
    type: ArgumentType<T>,
    getter: ArgumentGetter<S, V>,
    suggestions: (SuggestionsDsl<S>.() -> Unit)? = null
) = KrakenArgument.Required(name, this, type, getter, if (suggestions == null) null else { ctx, builder ->
    val dsl = SuggestionsDsl(ctx, builder)
    dsl.suggestions()
    builder.buildFuture()
})

fun <T : Any?, V : Any?> KrakenArgument.Required<CommandSourceStack, T, V>.optional(default: ArgumentGetter<CommandSourceStack, V?> = { _, _ -> null }) =
    KrakenArgument.Optional(this, type, default).required()

fun <T, V> KrakenArgument<CommandSourceStack, T, V>.required() = apply {
    (command as? KrakenDslCommand? ?: error("Argument $name: Only available for DSL.")).argument(this)
}
