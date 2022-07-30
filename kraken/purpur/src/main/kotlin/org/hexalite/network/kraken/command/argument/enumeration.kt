package org.hexalite.network.kraken.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.commands.CommandSourceStack
import org.hexalite.network.common.util.snakecase
import org.hexalite.network.kraken.command.dsl.CommandArgumentsScope
import org.hexalite.network.kraken.command.dsl.SuggestionsDsl
import kotlin.reflect.KClass

inline fun <reified E : Enum<E>> CommandArgumentsScope.enumeration(
    name: String,
    crossinline transform: (e: E) -> String = { it.name.snakecase() },
    noinline suggestions: (SuggestionsDsl<CommandSourceStack>.() -> Unit)? = null
) = command.createArgument(
    name,
    EnumerationArgument<E>(buildMap {
        enumValues<E>().forEach {
            set(transform(it), it)
        }
    }),
    { ctx, name -> EnumerationArgument[ctx, name, E::class] },
    suggestions
)

class EnumerationArgument<E : Enum<E>>(val values: Map<String, E>) : ArgumentType<E> {
    companion object {
        operator fun <E : Enum<E>> get(context: CommandContext<*>, name: String, kotlinClass: KClass<E>): E =
            context.getArgument(name, kotlinClass.java)
    }

    override fun parse(reader: StringReader): E {
        val word = reader.readUnquotedString()
        return values[word] ?: throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, word)
    }
}
