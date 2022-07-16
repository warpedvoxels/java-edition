package org.hexalite.discord.common.modal

import dev.kord.core.entity.interaction.ModalSubmitInteraction
import org.hexalite.discord.common.HexaliteClient
import org.hexalite.discord.common.InteractionContext
import org.hexalite.discord.common.modal.options.ModalArguments

class ModalSubmitContext<T : ModalArguments>(
    override val interaction: ModalSubmitInteraction,
    hexalite: HexaliteClient
) : InteractionContext(interaction, hexalite) {
    lateinit var argument: T

    fun populateArguments(args: T) {
        argument = args.also {
            it.options.forEach { option ->
                option.parsedValue = option.parse(interaction)
            }
        }
    }
}