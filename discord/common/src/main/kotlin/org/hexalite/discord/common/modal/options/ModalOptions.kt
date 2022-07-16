@file:Suppress("UNCHECKED_CAST")

package org.hexalite.discord.common.modal.options

import dev.kord.common.entity.TextInputStyle
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import kotlin.reflect.KProperty

sealed class ModalOption<T>(
    val customId: String,
    val label: String
) : ModalOptionBuilder {
    override var actionRowNumber: Int = 0
    override var value: String? = null
    var required: Boolean = true

    var parsedValue: T? = null

    operator fun getValue(thisRef: ModalArguments, property: KProperty<*>): T = parsedValue as T

    abstract fun parse(interaction: ModalSubmitInteraction): T
}

class TextInputOption<T : String?>(customId: String, label: String, val style: TextInputStyle) :
    ModalOption<T>(customId, label), TextInputOptionBuilder {
    override var allowedLength: ClosedRange<Int>? = null
    override var placeholder: String? = null

    override fun parse(interaction: ModalSubmitInteraction): T {
        return interaction.textInputs[customId]?.value as T
    }
}