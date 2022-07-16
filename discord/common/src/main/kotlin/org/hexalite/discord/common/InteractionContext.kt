package org.hexalite.discord.common

import dev.kord.core.behavior.interaction.ModalParentInteractionBehavior
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.PopupInteractionResponseBehavior
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import org.hexalite.discord.common.modal.ModalSubmitBuilder
import org.hexalite.discord.common.modal.ModalSubmitData
import org.hexalite.discord.common.modal.ModalSubmitRegistry
import org.hexalite.discord.common.modal.options.ModalArguments
import org.hexalite.discord.common.modal.options.TextInputOption
import org.hexalite.discord.common.utils.InteractionException

/**
 * @param interaction The interaction response received from [InteractionCreateEvent]
 */
@Suppress("UNCHECKED_CAST")
open class InteractionContext(
    open val interaction: Interaction,
    val hexalite: HexaliteClient
) {
    fun failPublic(builder: InteractionResponseCreateBuilder.() -> Unit): Nothing =
        throw InteractionException(
            ephemeral = false,
            isOriginalInteractionResponse = true,
            builder as MessageCreateBuilder.() -> Unit
        )

    fun failPublicFollowUp(builder: FollowupMessageCreateBuilder.() -> Unit): Nothing =
        throw InteractionException(
            ephemeral = false,
            isOriginalInteractionResponse = false,
            builder as MessageCreateBuilder.() -> Unit
        )

    fun failEphemeral(builder: InteractionResponseCreateBuilder.() -> Unit): Nothing =
        throw InteractionException(
            ephemeral = true,
            isOriginalInteractionResponse = true,
            builder as MessageCreateBuilder.() -> Unit
        )

    fun failEphemeralFollowUp(builder: FollowupMessageCreateBuilder.() -> Unit): Nothing =
        throw InteractionException(
            ephemeral = true,
            isOriginalInteractionResponse = false,
            builder as MessageCreateBuilder.() -> Unit
        )

    suspend inline fun <T : ModalArguments> modal(
        title: String,
        customId: String,
        noinline arguments: () -> T,
        block: ModalSubmitBuilder<T>.() -> Unit
    ): PopupInteractionResponseBehavior {
        val builder = ModalSubmitBuilder(customId, arguments).apply(block)
        builder.validate()

        val popupInteraction = (interaction as ModalParentInteractionBehavior).modal(title, customId) {
            val actionRows = mutableListOf(
                ActionRowBuilder(),
                ActionRowBuilder(),
                ActionRowBuilder(),
                ActionRowBuilder(),
                ActionRowBuilder()
            )
            val options = arguments.invoke().options

            options.forEach {
                actionRows[it.actionRowNumber].apply {
                    when (it) {
                        is TextInputOption -> {
                            textInput(it.style, it.customId, it.label) {
                                required = it.required
                                value = it.value
                                allowedLength = it.allowedLength
                                placeholder = it.placeholder
                            }
                        }
                    }
                }
            }

            components.addAll(actionRows.filter { it.components.isNotEmpty() })
        }

        ModalSubmitRegistry.register(builder.build() as ModalSubmitData<ModalArguments>)

        return popupInteraction
    }
}