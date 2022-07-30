package org.hexalite.network.kraken.command.dsl

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import net.kyori.adventure.text.Component
import net.minecraft.commands.CommandSourceStack
import org.bukkit.command.CommandException
import org.hexalite.network.common.util.Either
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.command.KrakenArgument
import org.hexalite.network.kraken.command.KrakenCommand
import org.hexalite.network.kraken.command.SuggestionsProvider
import org.hexalite.network.kraken.command.annotations.CommandDslMarker
import org.hexalite.network.kraken.command.argument.enumeration
import org.hexalite.network.kraken.command.argument.integer
import org.hexalite.network.kraken.command.argument.player
import org.hexalite.network.kraken.extension.registerCommand
import org.hexalite.network.kraken.extension.reply
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

typealias KrakenCommandDsl = KrakenDslCommand.() -> Unit
typealias BrigadierExecuteDsl<A> = (CommandContext<CommandSourceStack>).(arguments: A?) -> Int

class KrakenDslCommand(
    override var labels: MutableList<String> = mutableListOf(),
    override var permission: String = "",
    override var description: String = "",
) : KrakenCommand<CommandSourceStack> {

    val name get() = labels.first()
    private var rootCommand: Command<CommandSourceStack> = Command {
        currentContext = it
        Command.SINGLE_SUCCESS
    }

    private val arguments: MutableSet<KrakenArgument<CommandSourceStack, *, *>> = linkedSetOf()
    private val children: MutableSet<KrakenCommand<CommandSourceStack>> = linkedSetOf()
    private var currentContext: CommandContext<CommandSourceStack>? = null
    var registerAtRoot = false

    @Suppress("NAME_SHADOWING")
    override fun buildLiteral(): LiteralCommandNode<CommandSourceStack> {
        val brigadier = LiteralArgumentBuilder.literal<CommandSourceStack>(name).apply {
            children.forEach {
                then(it.buildLiteral())
            }
            if (registerAtRoot) {
                executes(rootCommand)
            }
            if (permission.isNotBlank()) {
                requires {
                    it.hasPermission(4, permission)
                }
            }
        }
        val root = brigadier.build().apply {
            if (arguments.isNotEmpty()) {
                val fragments = arrayOfNulls<ArgumentBuilder<CommandSourceStack, *>>(arguments.size)
                val arguments = arguments.sortedBy { if (it is KrakenArgument.Required) 0 else 1 }
                val argc = arguments.size - 1

                for (index in argc downTo 0) {
                    val argument = arguments.elementAt(index)
                    val builder = argument.brigadier().apply {
                        if (this@KrakenDslCommand.permission.isNotBlank()) {
                            requires {
                                it.hasPermission(4, "$permission.${argument.name}")
                            }
                        }
                    }
                    if (index == argc) {
                        builder.executes(rootCommand)
                    } else {
                        val i = index + 1
                        val argument = arguments.elementAt(i)
                        val element = fragments.elementAt(i)
                        if (argument is KrakenArgument.Optional) {
                            element!!.executes(rootCommand)
                            builder.executes(rootCommand).then(element)
                        } else {
                            builder.then(element)
                        }
                    }
                    fragments[index] = builder
                }
                addChild(fragments.first()!!.build())
            }
        }
        return root
    }

    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    fun <A : CommandArgumentsScope> executes(
        arguments: A? = null,
        root: Boolean = registerAtRoot,
        command: BrigadierExecuteDsl<A>
    ) {
        contract {
            callsInPlace(command, InvocationKind.AT_LEAST_ONCE)
        }
        registerAtRoot = root
        rootCommand = Command<CommandSourceStack> {
            currentContext = it
            it.command(arguments)
        }
    }

    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    fun <A : CommandArgumentsScope> runs(
        arguments: A? = null,
        root: Boolean = registerAtRoot,
        command: (CommandContext<CommandSourceStack>).(arguments: A?) -> Unit
    ) {
        contract {
            callsInPlace(command, InvocationKind.AT_LEAST_ONCE)
        }
        return executes(arguments, root) {
            command(it)
            Command.SINGLE_SUCCESS
        }
    }

    @CommandDslMarker
    fun children(name: String, block: KrakenCommandDsl) =
        KrakenDslCommand(mutableListOf(name)).apply(block).also(children::add)

    /**
     * Adds an [KrakenArgument] to this command
     *
     * @param argument to be added
     * @throws IllegalArgumentException if the last argument (excluding this one) is a greedy string
     */
    fun <T, V> argument(argument: KrakenArgument<CommandSourceStack, T, V>) {
        require(arguments.lastOrNull()?.type != StringArgumentType.greedyString()) {
            "You can't add new arguments after a greedy string."
        }
        arguments.add(argument)
    }

    operator fun <T, V> KrakenArgument<CommandSourceStack, T, V>.provideDelegate(
        thisRef: Any?,
        property: KProperty<*>
    ): ReadOnlyProperty<Any?, V> =
        ReadOnlyProperty { _, _ ->
            get(
                currentContext
                    ?: error("KrakenDslCommand: Context is not initialized. Could not get argument value for ${property::name}.")
            )
        }
}

@JvmInline
value class CommandRegisteringScope(val plugin: KrakenPlugin) {
    @CommandDslMarker
    fun command(labels: List<String>, block: KrakenCommandDsl) = KrakenDslCommand(labels.toMutableList()).apply(block)
        .also(plugin::registerCommand)

    @CommandDslMarker
    fun command(name: String, block: KrakenCommandDsl) = KrakenDslCommand(mutableListOf(name)).apply(block)
        .also(plugin::registerCommand)
}

abstract class CommandArgumentsScope(val command: KrakenCommand<CommandSourceStack>)

class SuggestionsDsl<S>(val ctx: CommandContext<S>, val builder: SuggestionsBuilder) {
    fun suggest(vararg values: Any) = values.forEach { value ->
        when (value) {
            is Int -> builder.suggest(value)
            is SuggestionsBuilder -> builder.add(value)
            else -> builder.suggest(value.toString())
        }
    }
}

//sealed class CommandExecutionException(
//    exceptionMessage: String? = null,
//    cause: Throwable? = null,
//) : RuntimeException(message = exceptionMessage, cause = cause, writableStackTrace = false, enableSuppression = false) {
//    class CommandNotFound(val commandName: String) : CommandExecutionException()
//
//    class Failure(val minecraftMessage: Component) : CommandException()
//
//    // Null as to not generate stack trace (way too expensive)
//    override fun fillInStackTrace(): Throwable? {
//        return null
//    }
//}
//
//private enum class GreetingKind {
//    Hello,
//    Hi
//}
//
//fun CommandRegisteringScope.hello() = command("hello") {
//    class Arguments : CommandArgumentsScope(this) {
//        val player by player("player")
//        val kind by enumeration<GreetingKind>("kind")
//        val times by integer("times") { suggest(1, 2, 3) }
//    }
//    runs(Arguments()) { args ->
//        val greeting = when(args!!.kind) {
//            GreetingKind.Hello -> "Hello, "
//            GreetingKind.Hi -> "Hi, "
//        }
//        repeat(args.times) { count ->
//            val component = Component.text("[$count] $greeting, ${args.player.name}!")
//            args.player.sendMessage(component)
//        }
//    }
//}
