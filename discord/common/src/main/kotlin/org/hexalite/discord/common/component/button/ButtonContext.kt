package org.hexalite.discord.common.component.button

import dev.kord.core.entity.interaction.ButtonInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.component.ComponentContext

class ButtonContext(
    override val interaction: ButtonInteraction,
    hexalite: HexaliteClient
) : ComponentContext(interaction, hexalite)