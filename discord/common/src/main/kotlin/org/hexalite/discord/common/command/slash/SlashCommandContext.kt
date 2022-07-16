package org.hexalite.discord.common.command.slash

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.command.CommandContext
import org.hexalite.discord.common.command.slash.options.SlashCommandArguments

class SlashCommandContext<T : SlashCommandArguments>(
    override val interaction: ChatInputCommandInteraction,
    hexalite: HexaliteClient
) : CommandContext(interaction, hexalite) {
    lateinit var argument: T

    fun populateArguments(args: T) {
        argument = args.also {
            it.options.forEach { option ->
                option.parsedValue = option.parse(interaction)
            }
        }
    }
}