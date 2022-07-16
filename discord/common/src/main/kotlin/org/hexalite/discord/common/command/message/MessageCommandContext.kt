package org.hexalite.discord.common.command.message

import dev.kord.core.entity.interaction.MessageCommandInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.command.CommandContext

class MessageCommandContext(
    override val interaction: MessageCommandInteraction,
    hexalite: HexaliteClient
) : CommandContext(interaction, hexalite)