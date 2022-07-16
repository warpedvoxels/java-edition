package org.hexalite.discord.common.component

import dev.kord.core.entity.interaction.ComponentInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.InteractionContext

open class ComponentContext(
    override val interaction: ComponentInteraction,
    hexalite: HexaliteClient
) : InteractionContext(interaction, hexalite)