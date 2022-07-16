package org.hexalite.discord.common.autocomplete

import dev.kord.core.entity.interaction.AutoCompleteInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.InteractionContext

class AutoCompleteContext(
    override val interaction: AutoCompleteInteraction,
    hexalite: HexaliteClient
) : InteractionContext(interaction, hexalite)