package org.hexalite.discord.common.component.selectmenu

import org.hexalite.discord.common.component.ComponentContext
import org.hexalite.discord.common.component.MessageComponentData

data class SelectMenuComponentData(
    override val customId: String,
    override val executor: suspend (ComponentContext).() -> Unit
) : MessageComponentData