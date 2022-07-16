package org.hexalite.discord.common.component

import dev.kord.core.entity.interaction.ComponentInteraction
import org.hexalite.discord.common.DiscordCommonData
import org.hexalite.discord.common.InteractionContext

open class ComponentContext(
    override val interaction: ComponentInteraction,
    hexalite: DiscordCommonData
) : InteractionContext(interaction, hexalite)