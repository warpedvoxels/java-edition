package org.hexalite.discord.common.autocomplete

import dev.kord.core.entity.interaction.AutoCompleteInteraction
import org.hexalite.discord.common.DiscordCommonData
import org.hexalite.discord.common.InteractionContext

class AutoCompleteContext(
    override val interaction: AutoCompleteInteraction,
    hexalite: DiscordCommonData
) : InteractionContext(interaction, hexalite)