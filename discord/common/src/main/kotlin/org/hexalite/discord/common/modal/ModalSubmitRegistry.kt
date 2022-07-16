package org.hexalite.discord.common.modal

import dev.kord.core.entity.interaction.ModalSubmitInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.modal.options.ModalArguments

interface ModalSubmitRegistry {
    val hexalite: HexaliteClient

    companion object {
        val modals: MutableList<ModalSubmitData<ModalArguments>> = mutableListOf()

        fun register(modal: ModalSubmitData<ModalArguments>) {
            if (modals.any { it.customId == modal.customId })
                error("Duplicated component with id: ${modal.customId}")

            modals.add(modal)
        }
    }

    fun findModal(interaction: ModalSubmitInteraction): ModalSubmitData<ModalArguments> {
        return modals.find { it.customId == interaction.modalId }
            ?: error("The modal id ${interaction.modalId} could not be found")
    }

    suspend fun executeModal(modal: ModalSubmitData<ModalArguments>, interaction: ModalSubmitInteraction) {
        val context = ModalSubmitContext<ModalArguments>(interaction, hexalite)
        context.populateArguments(modal.arguments.invoke())

        modal.executor.invoke(context)
    }
}