package org.hexalite.network.kraken.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSourceStack
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.command.brigadier.bukkit.BukkitBrigadierCommandWrapper

interface KrakenCommand<S> {
    /**
     * A simple list of all names that this command can be called with. (não se termina frase com preposição kkkkkkkkk
     */
    val labels: List<String>

    /**
     * The description of this command.
     */
    val description: String

    /**
     * The base permission for this command.
     */
    val permission: String

    /**
     * Creates a [literal][LiteralArgumentBuilder] from this [KrakenCommand]. A literal can be used to register
     * a specific command to the `minecraft` namespace in a [command dispatcher][CommandDispatcher].
     * @return the built [literal][LiteralArgumentBuilder].
     */
    fun buildLiteral(): LiteralCommandNode<S>
}

inline fun KrakenCommand<CommandSourceStack>.buildBukkit(plugin: KrakenPlugin) = BukkitBrigadierCommandWrapper(plugin, this)