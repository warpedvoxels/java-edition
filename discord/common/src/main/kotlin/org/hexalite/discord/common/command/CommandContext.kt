package org.hexalite.discord.common.command

import dev.kord.core.entity.interaction.ApplicationCommandInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.InteractionContext

open class CommandContext(
    override val interaction: ApplicationCommandInteraction,
    hexalite: HexaliteClient
) : InteractionContext(interaction, hexalite)