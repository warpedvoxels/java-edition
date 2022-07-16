package org.hexalite.discord.common.component

import dev.kord.common.entity.ButtonStyle
import dev.kord.rest.builder.component.ActionRowBuilder
import org.hexalite.discord.common.component.button.ButtonComponentBuilder
import org.hexalite.discord.common.component.selectmenu.SelectMenuComponentBuilder

inline fun ActionRowBuilder.interactiveButton(customId: String, style: ButtonStyle, block: ButtonComponentBuilder.() -> Unit) {
    val builder = ButtonComponentBuilder(customId).apply(block)
    builder.validate()

    interactionButton(style, customId) {
        this.style = style
        label = builder.label
        emoji = builder.emoji
        disabled = builder.disabled
    }

    ComponentRegistry.register(builder.build())
}

inline fun ActionRowBuilder.interactiveSelectMenu(customId: String, block: SelectMenuComponentBuilder.() -> Unit) {
    val builder = SelectMenuComponentBuilder(customId).apply(block)
    builder.validate()

    selectMenu(customId) {
        disabled = builder.disabled
        allowedValues = builder.allowedValues
        placeholder = builder.placeholder
        options.addAll(builder.options)
    }

    ComponentRegistry.register(builder.build())
}