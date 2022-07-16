package org.hexalite.discord.common.command.slash

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import org.hexalite.discord.common.DiscordCommonData
import org.hexalite.discord.common.command.CommandContext
import org.hexalite.discord.common.command.slash.options.SlashCommandArguments

class SlashCommandContext<T : SlashCommandArguments>(
    override val interaction: ChatInputCommandInteraction,
    hexalite: DiscordCommonData
) : CommandContext(interaction, hexalite) {
    lateinit var arguments: T

    fun populateArguments(args: T) {
        arguments = args.also {
            it.options.forEach { option ->
                option.parsedValue = option.parse(interaction)
            }
        }
    }
}