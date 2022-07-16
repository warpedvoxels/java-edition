package org.hexalite.discord.common.component.button

import dev.kord.common.entity.DiscordPartialEmoji
import org.hexalite.discord.common.component.ComponentBuilder
import org.hexalite.discord.common.component.ComponentContext

class ButtonComponentBuilder(private val customId: String) : ComponentBuilder {
    var label: String? = null
    var emoji: DiscordPartialEmoji? = null
    override var disabled: Boolean? = null

    private lateinit var executor: suspend (ButtonContext).() -> Unit

    fun onClick(block: suspend (ButtonContext).() -> Unit) {
        executor = block
    }

    fun validate() {
        if (label == null && emoji == null)
            error("The $customId button needs a label or emoji to be sent")
    }

    @Suppress("UNCHECKED_CAST")
    override fun build(): ButtonComponentData = ButtonComponentData(
        customId,
        executor as suspend (ComponentContext).() -> Unit
    )
}