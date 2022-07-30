package org.hexalite.network.kraken.command.brigadier.bukkit

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent
import net.minecraft.commands.CommandSourceStack
import org.bukkit.event.EventHandler
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.OpenBukkitEventListener

class BukkitBrigadierCompletion(override val plugin: KrakenPlugin) : OpenBukkitEventListener(plugin) {
    @EventHandler
    @Suppress("DEPRECATION")
    fun onBrigadierCompletion(event: CommandRegisteredEvent<CommandSourceStack>) {
        event.literal = (event.command as? BukkitBrigadierCommandWrapper? ?: return).kraken.buildLiteral()
    }
}