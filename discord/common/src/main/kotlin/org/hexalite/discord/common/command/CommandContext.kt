package org.hexalite.discord.common.command

import dev.kord.core.entity.interaction.ApplicationCommandInteraction
import org.hexalite.discord.common.DiscordCommonData
import org.hexalite.discord.common.InteractionContext

open class CommandContext(
    override val interaction: ApplicationCommandInteraction,
    hexalite: DiscordCommonData
) : InteractionContext(interaction, hexalite)