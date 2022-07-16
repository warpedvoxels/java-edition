package org.hexalite.discord.common.component.selectmenu

import dev.kord.core.entity.interaction.SelectMenuInteraction
import org.hexalite.discord.common.DiscordCommonData
import org.hexalite.discord.common.component.ComponentContext

class SelectMenuContext(
    override val interaction: SelectMenuInteraction,
    hexalite: DiscordCommonData
) : ComponentContext(interaction, hexalite)