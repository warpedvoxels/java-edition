package org.hexalite.network.kraken.command.dsl

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSourceStack
import org.hexalite.network.kraken.command.KrakenArgument
import org.hexalite.network.kraken.command.KrakenCommand
import org.hexalite.network.kraken.command.annotations.CommandDslMarker
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

typealias KrakenCommandDsl = KrakenDslCommand.() -> Unit
typealias BrigadierExecuteDsl = CommandContext<CommandSourceStack>.() -> Int

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
        val exec: BrigadierExecuteDsl = {
            rootCommand.run(this)
        }
        val brigadier = LiteralArgumentBuilder.literal<CommandSourceStack>(name).apply {
            children.forEach {
                then(it.buildLiteral())
            }
            if (registerAtRoot) {
                executes(exec)
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
                        builder.executes(exec)
                    } else {
                        val i = index + 1
                        val argument = arguments.elementAt(i)
                        val element = fragments.elementAt(i)
                        if (argument is KrakenArgument.Optional) {
                            element!!.executes(exec)
                            builder.executes(exec).then(element)
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
    fun executes(root: Boolean = registerAtRoot, command: BrigadierExecuteDsl) {
        contract {
            callsInPlace(command, InvocationKind.AT_LEAST_ONCE)
        }
        registerAtRoot = root
        rootCommand = Command<CommandSourceStack> {
            currentContext = it
            command(it)
        }
    }

    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    fun runs(root: Boolean = registerAtRoot, command: BrigadierExecuteDsl) {
        contract {
            callsInPlace(command, InvocationKind.AT_LEAST_ONCE)
        }
        return executes(root) {
            command()
            Command.SINGLE_SUCCESS
        }
    }

    @CommandDslMarker
    fun children(name: String, block: KrakenCommandDsl) = KrakenDslCommand(mutableListOf(name)).apply(block).also(children::add)

    fun <T, V> argument(argument: KrakenArgument<CommandSourceStack, T, V>) = arguments.add(argument)

    operator fun <T, V> KrakenArgument<CommandSourceStack, T, V>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, V> =
        ReadOnlyProperty { _, _ ->
            get(
                currentContext ?: error("KrakenDslCommand: Context is not initialized. Could not get argument value for ${property::name}.")
            )
        }
}

@CommandDslMarker
fun command(labels: List<String>, block: KrakenCommandDsl) = KrakenDslCommand(labels.toMutableList()).apply(block)

@CommandDslMarker
fun command(name: String, block: KrakenCommandDsl) = KrakenDslCommand(mutableListOf(name)).apply(block)
