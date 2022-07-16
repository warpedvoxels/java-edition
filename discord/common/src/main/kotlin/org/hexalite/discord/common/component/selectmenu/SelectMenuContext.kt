package org.hexalite.discord.common.component.selectmenu

import dev.kord.core.entity.interaction.SelectMenuInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.component.ComponentContext

class SelectMenuContext(
    override val interaction: SelectMenuInteraction,
    hexalite: HexaliteClient
) : ComponentContext(interaction, hexalite)