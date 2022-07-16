package org.hexalite.discord.common

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.FollowupPermittingInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.createEphemeralFollowup
import dev.kord.core.behavior.interaction.response.createPublicFollowup
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.interaction.ApplicationCommandInteractionCreateEvent
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.ComponentInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.on
import org.hexalite.discord.common.autocomplete.AutoCompleteRegistry
import org.hexalite.discord.common.command.ApplicationCommandData
import org.hexalite.discord.common.command.CommandRegistry
import org.hexalite.discord.common.command.slash.CommandWithArguments
import org.hexalite.discord.common.command.slash.options.SlashCommandArguments
import org.hexalite.discord.common.component.ComponentRegistry
import org.hexalite.discord.common.component.MessageComponentData
import org.hexalite.discord.common.modal.ModalSubmitData
import org.hexalite.discord.common.modal.ModalSubmitRegistry
import org.hexalite.discord.common.modal.options.ModalArguments
import org.hexalite.discord.common.utils.InteractionException

class InteractionRegistry(
    override val kord: Kord,
    override val hexalite: DiscordCommonData
) : CommandRegistry, ComponentRegistry, ModalSubmitRegistry, AutoCompleteRegistry {
    override val commands: MutableList<ApplicationCommandData> = mutableListOf()

private fun startListening() {
        kord.on<ApplicationCommandInteractionCreateEvent> {
            val command = findCommand(interaction)
            executeInteraction(command, interaction)
        }
        kord.on<ComponentInteractionCreateEvent> {
            val component = findComponent(interaction)
            executeInteraction(component, interaction)
        }
        kord.on<ModalSubmitInteractionCreateEvent> {
            val modal = findModal(interaction)
            executeInteraction(modal, interaction)
        }
        kord.on<AutoCompleteInteractionCreateEvent> {
            val autoCompleteCommand = findAutoComplete(interaction)
            executeInteraction(autoCompleteCommand, interaction)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun executeInteraction(data: InteractionData, interaction: Interaction) {
        kotlin.runCatching {
            when (interaction) {
                is ApplicationCommandInteraction -> {
                    executeCommand(data as ApplicationCommandData, interaction)
                }
                is ComponentInteraction -> {
                    executeComponent(data as MessageComponentData, interaction)
                }
                is ModalSubmitInteraction -> {
                    executeModal(data as ModalSubmitData<ModalArguments>, interaction)
                }
                is AutoCompleteInteraction -> {
                    executeAutoComplete(data as CommandWithArguments<out SlashCommandArguments>, interaction)
                }
            }
        }.onFailure {
            if (it is InteractionException && interaction !is AutoCompleteInteraction)
                handleOnFailure(it, interaction as ActionInteraction)
            else throw it
        }
    }

    private suspend fun handleOnFailure(exception: InteractionException, interaction: ActionInteraction) {
        if (exception.isOriginalInteractionResponse) {
            if (exception.ephemeral)
                interaction.respondEphemeral(exception.builder)
            else interaction.respondPublic(exception.builder)
        } else {
            interaction as FollowupPermittingInteractionResponseBehavior

            if (exception.ephemeral)
                interaction.createEphemeralFollowup(exception.builder)
            else interaction.createPublicFollowup(exception.builder)
        }
    }

    suspend fun start() {
        registerDiscordCommands()
        startListening()
        kord.login()
    }
}