package org.hexalite.discord.common.component

import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.core.entity.interaction.SelectMenuInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.component.button.ButtonContext
import org.hexalite.discord.common.component.selectmenu.SelectMenuContext

interface ComponentRegistry {
    val hexalite: HexaliteClient

    companion object {
        val components: MutableList<MessageComponentData> = mutableListOf()

        fun register(component: MessageComponentData) {
            if (components.any { it.customId == component.customId })
                error("Duplicated component with id: ${component.customId}")

            components.add(component)
        }
    }

    fun findComponent(interaction: ComponentInteraction): MessageComponentData {
        return components.find { it.customId == interaction.componentId }
            ?: error("The component id ${interaction.componentId} could not be found")
    }

    suspend fun executeComponent(component: MessageComponentData, interaction: ComponentInteraction) {
        val context = when (interaction) {
            is ButtonInteraction -> ButtonContext(interaction, hexalite)
            is SelectMenuInteraction -> SelectMenuContext(interaction, hexalite)
        }

        component.executor.invoke(context)
    }
}