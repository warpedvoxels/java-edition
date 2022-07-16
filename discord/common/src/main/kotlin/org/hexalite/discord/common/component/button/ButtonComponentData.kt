package org.hexalite.discord.common.component.button

import org.hexalite.discord.common.component.ComponentContext
import org.hexalite.discord.common.component.MessageComponentData

data class ButtonComponentData(
    override val customId: String,
    override val executor: suspend (ComponentContext).() -> Unit
) : MessageComponentData