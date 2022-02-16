package org.hexalite.network.kraken.command.brigadier.bukkit

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.craftbukkit.v1_18_R1.command.VanillaCommandWrapper
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.command.KrakenCommand
import kotlin.math.min

inline fun CommandDispatcher<CommandSourceStack>.run(sender: CommandSourceStack, vararg arguments: String) =
    execute(arguments.joinToString(" "), sender)

inline fun CommandSender.toStack(): CommandSourceStack =
    VanillaCommandWrapper.getListener(this)

class BukkitBrigadierCommandWrapper(
    private val _plugin: KrakenPlugin,
    val kraken: KrakenCommand<CommandSourceStack>
) : PluginIdentifiableCommand,
    BukkitCommand(
        kraken.labels.first(), kraken.description, "", kraken.labels.drop(1)
    ) {

    override fun getPlugin() = _plugin

    companion object {
        private val dispatcher = CommandDispatcher<CommandSourceStack>()
    }

    init {
        dispatcher.root.addChild(kraken.buildLiteral())
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        val stack = sender.toStack()
        try {
            dispatcher.run(stack, commandLabel, *args)
        } catch (ex: CommandSyntaxException) {
            stack.sendFailure(ComponentUtils.fromMessage(ex.rawMessage))
            if (ex.input != null && ex.cursor >= 0) {
                val cursor = min(ex.input.length, ex.cursor)
                val error = TextComponent("").withStyle(ChatFormatting.GRAY).withStyle {
                    it.withClickEvent(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, label))
                }
                if (cursor > 10) {
                    error.append("...")
                }
                error.append(ex.input.substring(0.coerceAtLeast(cursor - 10), cursor))
                if (cursor < ex.input.length) {
                    error.append(
                        TextComponent(ex.input.substring(cursor)).withStyle(
                            ChatFormatting.RED,
                            ChatFormatting.UNDERLINE
                        )
                    )
                }
                error.append(
                    (TranslatableComponent("command.context.here").withStyle(
                        ChatFormatting.RED,
                        ChatFormatting.ITALIC
                    ))
                )
                stack.sendFailure(error)
            }
        }
        return true
    }

}