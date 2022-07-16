package org.hexalite.discord.common.command.user

import dev.kord.core.entity.interaction.UserCommandInteraction
import org.hexalite.discord.common.DiscordCommonData
import org.hexalite.discord.common.command.CommandContext

class UserCommandContext(
    override val interaction: UserCommandInteraction,
    hexalite: DiscordCommonData
) : CommandContext(interaction, hexalite)