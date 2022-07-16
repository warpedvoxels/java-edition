package org.hexalite.discord.common.command.user

import dev.kord.core.entity.interaction.UserCommandInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.command.CommandContext

class UserCommandContext(
    override val interaction: UserCommandInteraction,
    hexalite: HexaliteClient
) : CommandContext(interaction, hexalite)