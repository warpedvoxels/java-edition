package org.hexalite.discord.common.modal.options

import dev.kord.common.entity.TextInputStyle

open class ModalArguments {
    val options: MutableList<ModalOption<Any?>> = mutableListOf()
}

fun ModalArguments.textInput(
    customId: String,
    label: String,
    style: TextInputStyle,
    builder: TextInputOptionBuilder.() -> Unit = {}
) = TextInputOption<String>(customId, label, style).apply(builder).also {
    register(it)
}

fun ModalArguments.optionalTextInput(
    customId: String,
    label: String,
    style: TextInputStyle,
    builder: TextInputOptionBuilder.() -> Unit = {}
) = TextInputOption<String?>(customId, label, style).apply(builder).also {
    register(it)
}

@Suppress("UNCHECKED_CAST")
private inline fun <reified T> ModalArguments.register(option: ModalOption<T>) {
    if (options.any { it.customId == option.customId })
        throw IllegalArgumentException("Duplicate argument \"${option.customId}\"!")

    option.validate()

    option.apply {
        this.required = null !is T
    }
    options.add(option as ModalOption<Any?>)
}